package me.jangluzniewicz.webstore.utils.testdata.carts;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;

@Builder
public class CartRequestTestDataBuilder {
  @Default private List<CartItemRequestTestDataBuilder> items = new ArrayList<>();

  public CartRequest buildCartRequest() {
    return new CartRequest(
        items.stream()
            .map(CartItemRequestTestDataBuilder::buildCartItemRequest)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
  }
}
