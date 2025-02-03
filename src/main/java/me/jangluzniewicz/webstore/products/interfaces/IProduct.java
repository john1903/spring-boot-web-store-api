package me.jangluzniewicz.webstore.products.interfaces;

import java.math.BigDecimal;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.models.Product;

public interface IProduct {
  Long createNewProduct(ProductRequest productRequest);

  Long updateProduct(Long id, ProductRequest productRequest);

  Optional<Product> getProductById(Long id);

  PagedResponse<Product> getAllProducts(Integer page, Integer size);

  PagedResponse<Product> getFilteredProducts(
      Long categoryId,
      String name,
      BigDecimal priceFrom,
      BigDecimal priceTo,
      Integer page,
      Integer size);

  void deleteProduct(Long id);
}
