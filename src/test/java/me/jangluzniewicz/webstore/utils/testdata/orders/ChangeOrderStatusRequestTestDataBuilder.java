package me.jangluzniewicz.webstore.utils.testdata.orders;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;

@Builder
public class ChangeOrderStatusRequestTestDataBuilder {
  @Default private Long orderStatusId = 1L;

  public ChangeOrderStatusRequest buildChangeOrderStatusRequest() {
    return new ChangeOrderStatusRequest(orderStatusId);
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
