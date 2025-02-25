package me.jangluzniewicz.webstore.utils.testdata.orders;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.OrderStatusRequest;

@Builder
public class OrderStatusRequestTestDataBuilder {
  @Default private Long orderStatusId = 1L;

  public OrderStatusRequest buildChangeOrderStatusRequest() {
    return new OrderStatusRequest(orderStatusId);
  }

  public String toJson() {
    return """
        {
          "orderStatusId": %d
        }
        """
        .formatted(orderStatusId);
  }
}
