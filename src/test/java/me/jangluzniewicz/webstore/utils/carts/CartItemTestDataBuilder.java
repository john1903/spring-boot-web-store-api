package me.jangluzniewicz.webstore.utils.carts;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.models.CartItem;
import me.jangluzniewicz.webstore.utils.products.ProductTestDataBuilder;

@Builder
public class CartItemTestDataBuilder {
  private Long id;
  @Default private ProductTestDataBuilder productBuilder = ProductTestDataBuilder.builder().build();
  @Default private int quantity = 1;

  public CartItem buildCartItem() {
    return CartItem.builder()
        .id(id)
        .product(productBuilder.buildProduct())
        .quantity(quantity)
        .build();
  }
}
