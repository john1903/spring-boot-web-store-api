package me.jangluzniewicz.webstore.commons.testdata.carts;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;

@Builder
public class CartItemRequestTestDataBuilder {
  @Default private Long id = 1L;
  @Default private Long productId = 1L;
  @Default private Integer quantity = 1;

  public CartItemRequest buildCartItemRequest() {
    return new CartItemRequest(id, productId, quantity);
  }
}
