package me.jangluzniewicz.webstore.common.testdata.orders;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.entities.OrderItemEntity;
import me.jangluzniewicz.webstore.common.testdata.order_statuses.OrderStatusEntityTestDataBuilder;
import me.jangluzniewicz.webstore.common.testdata.users.UserEntityTestDataBuilder;

@Builder
public class OrderEntityTestDataBuilder {
  private Long id;

  @Default
  private UserEntityTestDataBuilder userEntityBuilder = UserEntityTestDataBuilder.builder().build();

  @Default
  private OrderStatusEntityTestDataBuilder orderStatusEntityBuilder =
      OrderStatusEntityTestDataBuilder.builder().build();

  @Default private List<OrderItemEntity> items = new ArrayList<>();

  public OrderEntity buildOrderEntity() {
    return OrderEntity.builder()
        .id(id)
        .customer(userEntityBuilder.buildUserEntity())
        .status(orderStatusEntityBuilder.buildOrderStatusEntity())
        .items(items)
        .build();
  }
}
