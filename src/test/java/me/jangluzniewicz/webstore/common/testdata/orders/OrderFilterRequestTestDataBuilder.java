package me.jangluzniewicz.webstore.common.testdata.orders;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;

@Builder
public class OrderFilterRequestTestDataBuilder {
  @Default private Long statusId = 1L;
  @Default private LocalDateTime orderDateAfter = LocalDateTime.now().minusDays(1);
  @Default private LocalDateTime orderDateBefore = LocalDateTime.now().plusDays(1);

  public OrderFilterRequest buildOrderFilterRequest() {
    return new OrderFilterRequest(statusId, orderDateAfter, orderDateBefore);
  }
}
