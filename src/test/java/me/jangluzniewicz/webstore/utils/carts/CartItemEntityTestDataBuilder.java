package me.jangluzniewicz.webstore.utils.carts;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.entities.CartItemEntity;
import me.jangluzniewicz.webstore.utils.products.ProductEntityTestDataBuilder;

@Builder
public class CartItemEntityTestDataBuilder {
  private Long id;

  @Default
  private ProductEntityTestDataBuilder productBuilder =
      ProductEntityTestDataBuilder.builder().build();

  @Default private int quantity = 1;

  public CartItemEntity buildCartItemEntity() {
    return CartItemEntity.builder()
        .id(id)
        .product(productBuilder.buildProductEntity())
        .quantity(quantity)
        .build();
  }
}
