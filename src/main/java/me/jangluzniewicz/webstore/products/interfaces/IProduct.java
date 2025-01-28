package me.jangluzniewicz.webstore.products.interfaces;

import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.models.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IProduct {
    Long createNewProduct(ProductRequest productRequest);

    Long updateProduct(Long id, ProductRequest productRequest);

    Optional<Product> getProductById(Long id);

    List<Product> getAllProducts(Integer page, Integer size);

    List<Product> getFilteredProducts(Long categoryId, String name,
                                      BigDecimal priceFrom, BigDecimal priceTo,
                                      Integer page, Integer size);

    void deleteProduct(Long id);
}
