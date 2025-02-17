package me.jangluzniewicz.webstore.common.testdata.orders;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.OrderItemRequest;

@Builder
public class OrderItemRequestTestDataBuilder {
  @Default private Long id = 1L;
  @Default private Long productId = 1L;
  @Default Integer quantity = 1;

  public OrderItemRequest buildOrderItemRequest() {
    return new OrderItemRequest(id, productId, quantity);
  }
}
