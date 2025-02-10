package me.jangluzniewicz.webstore.common.testdata.order_statuses;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;

@Builder
public class OrderStatusTestDataBuilder {
  private Long id;
  @Default private String name = "PENDING";

  public OrderStatus buildOrderStatus() {
    return OrderStatus.builder().id(id).name(name).build();
  }
}
