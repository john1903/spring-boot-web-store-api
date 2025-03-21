package me.jangluzniewicz.webstore.utils.testdata.order_statuses;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orderstatuses.models.OrderStatus;

@Builder
public class OrderStatusTestDataBuilder {
  @Default private Long id = 1L;
  @Default private String name = "NOT_APPROVED";

  public OrderStatus buildOrderStatus() {
    return OrderStatus.builder().id(id).name(name).build();
  }
}
