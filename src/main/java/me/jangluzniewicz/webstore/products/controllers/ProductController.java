package me.jangluzniewicz.webstore.products.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {
  private final IProduct productService;

  public ProductController(IProduct productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<PagedResponse<Product>> getProducts(
      @Valid @ModelAttribute ProductFilterRequest filterRequest,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    return ResponseEntity.ok(productService.getFilteredProducts(filterRequest, page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProduct(@PathVariable Long id) {
    return productService
        .getProductById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Void> createProduct(@Valid @RequestBody ProductRequest productRequest) {
    Long productId = productService.createNewProduct(productRequest);
    return ResponseEntity.created(URI.create("/products/" + productId)).build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateProduct(
      @PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
    productService.updateProduct(id, productRequest);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}
