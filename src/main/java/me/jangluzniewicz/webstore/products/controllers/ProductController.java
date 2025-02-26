package me.jangluzniewicz.webstore.products.controllers;

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
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Products", description = "Operations related to products")
@RestController
@RequestMapping("/products")
public class ProductController {
  private final IProduct productService;

  public ProductController(IProduct productService) {
    this.productService = productService;
  }

  @Operation(
      summary = "Get products",
      description = "Returns a paginated list of products based on provided filter criteria")
  @ApiResponse(
      responseCode = "200",
      description = "List of products",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PagedResponse.class)))
  @GetMapping
  public ResponseEntity<PagedResponse<Product>> getProducts(
      @Valid @ModelAttribute ProductFilterRequest filterRequest,
      @Parameter(in = ParameterIn.QUERY, description = "Page number", example = "0")
          @RequestParam(defaultValue = "0")
          Integer page,
      @Parameter(in = ParameterIn.QUERY, description = "Page size", example = "20")
          @RequestParam(defaultValue = "20")
          Integer size) {
    return ResponseEntity.ok(productService.getFilteredProducts(filterRequest, page, size));
  }

  @Operation(
      summary = "Get product by ID",
      description = "Returns a product based on the provided ID")
  @ApiResponse(
      responseCode = "200",
      description = "Product found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Product.class)))
  @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
  @GetMapping("/{id}")
  public ResponseEntity<Product> getProduct(
      @Parameter(in = ParameterIn.PATH, description = "Product ID", required = true, example = "1")
          @PathVariable
          Long id) {
    return productService
        .getProductById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Operation(
      summary = "Create product",
      description = "Creates a new product (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "201",
      description = "Product created",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = IdResponse.class)))
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<IdResponse> createProduct(
      @RequestBody(
              description = "Product creation payload",
              required = true,
              content = @Content(schema = @Schema(implementation = ProductRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          ProductRequest productRequest) {
    IdResponse response = productService.createNewProduct(productRequest);
    return ResponseEntity.created(URI.create("/products/" + response.getId())).body(response);
  }

  @Operation(
      summary = "Import products from CSV",
      description =
          "Creates multiple products by importing data from a CSV file (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Products created from CSV", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/import/csv")
  public ResponseEntity<Void> createProductsFromCsv(
      @Parameter(
              in = ParameterIn.QUERY,
              description = "CSV file containing product data",
              required = true)
          @RequestParam("file")
          MultipartFile file) {
    productService.createNewProductsFromCsv(file);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Update product",
      description = "Updates an existing product (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Product updated", content = @Content)
  @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateProduct(
      @Parameter(in = ParameterIn.PATH, description = "Product ID", required = true, example = "1")
          @PathVariable
          Long id,
      @RequestBody(
              description = "Product update payload",
              required = true,
              content = @Content(schema = @Schema(implementation = ProductRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          ProductRequest productRequest) {
    productService.updateProduct(id, productRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Delete product",
      description = "Deletes a product by its ID (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Product deleted", content = @Content)
  @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
  @ApiResponse(
      responseCode = "409",
      description = "Product has associated orders",
      content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(
      @Parameter(in = ParameterIn.PATH, description = "Product ID", required = true, example = "1")
          @PathVariable
          Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}
