package me.jangluzniewicz.webstore.common.testdata.order_statuses;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.order_statuses.controllers.OrderStatusRequest;

@Builder
public class OrderStatusRequestTestDataBuilder {
  @Default private String name = "PENDING";

  public OrderStatusRequest buildOrderStatusRequest() {
    return new OrderStatusRequest(name);
  }
}
