package me.jangluzniewicz.webstore.common.testdata.orders;

import lombok.Builder;
import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;

@Builder
public class ChangeOrderStatusRequestTestDataBuilder {
  private Long orderStatusId;

  public ChangeOrderStatusRequest buildChangeOrderStatusRequest() {
    return new ChangeOrderStatusRequest(orderStatusId);
  }
}
