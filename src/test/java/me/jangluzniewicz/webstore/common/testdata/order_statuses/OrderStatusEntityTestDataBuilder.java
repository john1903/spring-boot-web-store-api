package me.jangluzniewicz.webstore.common.testdata.order_statuses;

import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;

@Builder
public class OrderStatusEntityTestDataBuilder {
  private Long id;
  @Default private String name = "PENDING";

  public OrderStatusEntity buildOrderStatusEntity() {
    return OrderStatusEntity.builder().id(id).name(name).build();
  }
}
