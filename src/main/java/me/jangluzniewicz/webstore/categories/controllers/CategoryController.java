package me.jangluzniewicz.webstore.categories.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Categories", description = "Operations related to categories")
@RestController
@RequestMapping("/categories")
public class CategoryController {
  private final ICategory categoryService;

  public CategoryController(ICategory categoryService) {
    this.categoryService = categoryService;
  }

  @Operation(summary = "Get categories", description = "Returns a paginated list of categories")
  @ApiResponse(
      responseCode = "200",
      description = "List of categories",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PagedResponse.class)))
  @GetMapping
  public ResponseEntity<PagedResponse<Category>> getCategories(
      @Parameter(in = ParameterIn.QUERY, description = "Page number", example = "0")
          @RequestParam(defaultValue = "0")
          Integer page,
      @Parameter(in = ParameterIn.QUERY, description = "Page size", example = "20")
          @RequestParam(defaultValue = "20")
          Integer size) {
    return ResponseEntity.ok(categoryService.getAllCategories(page, size));
  }

  @Operation(
      summary = "Get category by ID",
      description = "Returns a category based on the provided ID")
  @ApiResponse(
      responseCode = "200",
      description = "Category found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Category.class)))
  @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategory(
      @Parameter(in = ParameterIn.PATH, description = "Category ID", required = true, example = "1")
          @PathVariable
          Long id) {
    return categoryService
        .getCategoryById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new NotFoundException("Category not found"));
  }

  @Operation(
      summary = "Create category",
      description = "Creates a new category (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "201",
      description = "Category created",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = IdResponse.class)))
  @ApiResponse(
      responseCode = "409",
      description = "Category name already exists",
      content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<IdResponse> createCategory(
      @RequestBody(
              description = "Category creation payload",
              required = true,
              content = @Content(schema = @Schema(implementation = CategoryRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          CategoryRequest categoryRequest) {
    IdResponse response = categoryService.createNewCategory(categoryRequest);
    return ResponseEntity.created(URI.create("/categories/" + response.getId())).body(response);
  }

  @Operation(
      summary = "Update category",
      description = "Updates an existing category (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Category updated", content = @Content)
  @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
  @ApiResponse(
      responseCode = "409",
      description = "Category name already exists",
      content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateCategory(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the category to update",
              required = true,
              example = "1")
          @PathVariable
          Long id,
      @RequestBody(
              description = "Category update payload",
              required = true,
              content = @Content(schema = @Schema(implementation = CategoryRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          CategoryRequest categoryRequest) {
    categoryService.updateCategory(id, categoryRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Delete category",
      description = "Deletes a category by its ID (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Category deleted", content = @Content)
  @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
  @ApiResponse(responseCode = "409", description = "Category has products", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the category to delete",
              required = true,
              example = "1")
          @PathVariable
          Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}
