package me.jangluzniewicz.webstore.common.testdata.orders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.common.testdata.order_statuses.OrderStatusEntityTestDataBuilder;
import me.jangluzniewicz.webstore.common.testdata.users.UserEntityTestDataBuilder;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;

@Builder
public class OrderEntityTestDataBuilder {
  @Default private Long id = 1L;

  @Default
  private UserEntityTestDataBuilder userEntityBuilder = UserEntityTestDataBuilder.builder().build();

  @Default
  private OrderStatusEntityTestDataBuilder orderStatusEntityBuilder =
      OrderStatusEntityTestDataBuilder.builder().build();

  @Default
  private List<OrderItemEntityTestDataBuilder> items =
      List.of(OrderItemEntityTestDataBuilder.builder().build());

  private RatingEntityTestDataBuilder ratingEntityBuilder;

  public OrderEntity buildOrderEntity() {
    return OrderEntity.builder()
        .orderDate(LocalDateTime.now())
        .statusChangeDate(LocalDateTime.now())
        .id(id)
        .customer(userEntityBuilder.buildUserEntity())
        .status(orderStatusEntityBuilder.buildOrderStatusEntity())
        .rating(ratingEntityBuilder != null ? ratingEntityBuilder.buildRatingEntity() : null)
        .items(
            items.stream()
                .map(OrderItemEntityTestDataBuilder::buildOrderItemEntity)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll))
        .build();
  }
}
