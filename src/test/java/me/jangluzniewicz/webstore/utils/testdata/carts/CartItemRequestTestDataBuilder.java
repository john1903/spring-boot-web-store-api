package me.jangluzniewicz.webstore.utils.testdata.carts;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;

@Builder
public class CartItemRequestTestDataBuilder {
  private Long id;
  @Default private Long productId = 1L;
  @Default private Integer quantity = 1;

  public CartItemRequest buildCartItemRequest() {
    return new CartItemRequest(null, productId, quantity);
  }

  public String toJson() {
    return """
        {
          "productId": %d,
          "quantity": %d
        }
        """
        .formatted(productId, quantity);
  }
}
