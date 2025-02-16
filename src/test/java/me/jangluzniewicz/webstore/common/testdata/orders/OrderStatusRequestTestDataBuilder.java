package me.jangluzniewicz.webstore.common.testdata.orders;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.OrderStatusRequest;

@Builder
public class OrderStatusRequestTestDataBuilder {
  @Default private Long orderStatusId = 1L;

  public OrderStatusRequest buildChangeOrderStatusRequest() {
    return new OrderStatusRequest(orderStatusId);
  }
}
