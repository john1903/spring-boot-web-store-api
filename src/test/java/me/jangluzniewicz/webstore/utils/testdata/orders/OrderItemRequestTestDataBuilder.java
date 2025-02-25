package me.jangluzniewicz.webstore.utils.testdata.orders;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.OrderItemRequest;

@Builder
public class OrderItemRequestTestDataBuilder {
  private Long id;
  @Default private Long productId = 1L;
  @Default Integer quantity = 1;

  public OrderItemRequest buildOrderItemRequest() {
    return new OrderItemRequest(id, productId, quantity);
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
