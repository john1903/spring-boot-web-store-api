package me.jangluzniewicz.webstore.common.testdata.carts;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.entities.CartItemEntity;
import me.jangluzniewicz.webstore.common.testdata.products.ProductEntityTestDataBuilder;

@Builder
public class CartItemEntityTestDataBuilder {
  @Default private Long id = 1L;

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
