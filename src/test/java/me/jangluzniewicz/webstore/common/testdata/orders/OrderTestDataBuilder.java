package me.jangluzniewicz.webstore.common.testdata.orders;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.common.testdata.order_statuses.OrderStatusTestDataBuilder;
import me.jangluzniewicz.webstore.common.testdata.users.UserTestDataBuilder;
import me.jangluzniewicz.webstore.orders.models.Order;

@Builder
public class OrderTestDataBuilder {
  @Default private Long id = 1L;
  @Default private UserTestDataBuilder userBuilder = UserTestDataBuilder.builder().build();

  @Default
  private OrderStatusTestDataBuilder orderStatusBuilder =
      OrderStatusTestDataBuilder.builder().build();

  @Default
  private List<OrderItemTestDataBuilder> items =
      List.of(OrderItemTestDataBuilder.builder().build());

  private RatingTestDataBuilder ratingBuilder;

  public Order buildOrder() {
    return Order.builder()
        .id(id)
        .customer(userBuilder.buildUser())
        .status(orderStatusBuilder.buildOrderStatus())
        .rating(ratingBuilder != null ? ratingBuilder.buildRating() : null)
        .items(
            items.stream()
                .map(OrderItemTestDataBuilder::buildOrderItem)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll))
        .build();
  }
}
