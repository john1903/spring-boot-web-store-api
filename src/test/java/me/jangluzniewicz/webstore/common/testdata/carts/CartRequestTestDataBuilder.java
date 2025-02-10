package me.jangluzniewicz.webstore.common.testdata.carts;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;

@Builder
public class CartRequestTestDataBuilder {
  private Long userId;
  @Default private List<CartItemRequest> items = new ArrayList<>();

  public CartRequest buildCartRequest() {
    return new CartRequest(userId, items);
  }
}
