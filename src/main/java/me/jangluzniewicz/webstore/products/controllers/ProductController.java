package me.jangluzniewicz.webstore.products.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
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
  public ResponseEntity<IdResponse> createProduct(
      @Valid @RequestBody ProductRequest productRequest) {
    IdResponse response = productService.createNewProduct(productRequest);
    return ResponseEntity.created(URI.create("/products/" + response.getId())).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/import/csv")
  public ResponseEntity<Void> createProductsFromCsv(@RequestParam("file") MultipartFile file) {
    productService.createNewProductsFromCsv(file);
    return ResponseEntity.noContent().build();
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
