package me.jangluzniewicz.webstore.utils.testdata.order_statuses;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orderstatuses.controllers.OrderStatusRequest;

@Builder
public class OrderStatusRequestTestDataBuilder {
  @Default private String name = "NOT_APPROVED";

  public OrderStatusRequest buildOrderStatusRequest() {
    return new OrderStatusRequest(name);
  }

  public String toJson() {
    return """
    {
      "name": "%s"
    }
    """
        .formatted(name);
  }
}
