package me.jangluzniewicz.webstore.utils.orders;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.orders.models.OrderItem;
import me.jangluzniewicz.webstore.utils.order_statuses.OrderStatusTestDataBuilder;
import me.jangluzniewicz.webstore.utils.users.UserTestDataBuilder;

@Builder
public class OrderTestDataBuilder {
  private Long id;
  @Default private UserTestDataBuilder userBuilder = UserTestDataBuilder.builder().build();

  @Default
  private OrderStatusTestDataBuilder orderStatusBuilder =
      OrderStatusTestDataBuilder.builder().build();

  @Default private List<OrderItem> items = new ArrayList<>();

  public Order buildOrder() {
    return Order.builder()
        .id(id)
        .customer(userBuilder.buildUser())
        .status(orderStatusBuilder.buildOrderStatus())
        .items(items)
        .build();
  }
}
