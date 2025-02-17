package me.jangluzniewicz.webstore.common.testdata.order_statuses;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orderstatuses.entities.OrderStatusEntity;

@Builder
public class OrderStatusEntityTestDataBuilder {
  @Default private Long id = 1L;
  @Default private String name = "PENDING";

  public OrderStatusEntity buildOrderStatusEntity() {
    return OrderStatusEntity.builder().id(id).name(name).build();
  }
}
