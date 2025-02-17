package me.jangluzniewicz.webstore.products.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.models.Product;

public interface IProduct {
  IdResponse createNewProduct(@NotNull ProductRequest productRequest);

  void updateProduct(@NotNull @Min(1) Long id, @NotNull ProductRequest productRequest);

  Optional<Product> getProductById(@NotNull @Min(1) Long id);

  PagedResponse<Product> getAllProducts(
      @NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  PagedResponse<Product> getFilteredProducts(
      @NotNull ProductFilterRequest filter,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size);

  void deleteProduct(@NotNull @Min(1) Long id);
}
