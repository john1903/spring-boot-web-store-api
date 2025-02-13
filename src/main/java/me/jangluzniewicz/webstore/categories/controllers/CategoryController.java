package me.jangluzniewicz.webstore.categories.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {
  private final ICategory categoryService;

  public CategoryController(ICategory categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  public ResponseEntity<PagedResponse<Category>> getCategories(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    return ResponseEntity.ok(categoryService.getAllCategories(page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategory(@PathVariable Long id) {
    return categoryService
        .getCategoryById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Void> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
    Long categoryId = categoryService.createNewCategory(categoryRequest);
    return ResponseEntity.created(URI.create("/categories/" + categoryId)).build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateCategory(
      @PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
    categoryService.updateCategory(id, categoryRequest);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}
