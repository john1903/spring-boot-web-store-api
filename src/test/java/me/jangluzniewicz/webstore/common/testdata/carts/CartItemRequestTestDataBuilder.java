package me.jangluzniewicz.webstore.common.testdata.carts;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;

@Builder
public class CartItemRequestTestDataBuilder {
  private Long id;
  private Long productId;
  @Default private Integer quantity = 1;

  public CartItemRequest buildCartItemRequest() {
    return new CartItemRequest(id, productId, quantity);
  }
}
