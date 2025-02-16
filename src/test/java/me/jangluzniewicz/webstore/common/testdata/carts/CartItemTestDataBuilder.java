package me.jangluzniewicz.webstore.common.testdata.carts;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.models.CartItem;
import me.jangluzniewicz.webstore.common.testdata.products.ProductTestDataBuilder;

@Builder
public class CartItemTestDataBuilder {
  @Default private Long id = 1L;
  @Default private ProductTestDataBuilder productBuilder = ProductTestDataBuilder.builder().build();
  @Default private Integer quantity = 1;

  public CartItem buildCartItem() {
    return CartItem.builder()
        .id(id)
        .product(productBuilder.buildProduct())
        .quantity(quantity)
        .build();
  }
}
