package me.jangluzniewicz.webstore.common.testdata.orders;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.OrderItemRequest;

@Builder
public class OrderItemRequestTestDataBuilder {
  @Default private Long id = 1L;
  @Default private Long productId = 1L;
  @Default Integer quantity = 1;
  @Default private BigDecimal price = BigDecimal.valueOf(1000.0);
  @Default private BigDecimal discount = BigDecimal.ONE;

  public OrderItemRequest buildOrderItemRequest() {
    return new OrderItemRequest(id, productId, quantity, price, discount);
  }
}
