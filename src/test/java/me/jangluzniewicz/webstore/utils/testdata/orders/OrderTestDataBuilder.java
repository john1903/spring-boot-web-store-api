package me.jangluzniewicz.webstore.utils.testdata.orders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.utils.testdata.order_statuses.OrderStatusTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.users.UserTestDataBuilder;

@Builder
public class OrderTestDataBuilder {
  @Default private Long id = 1L;
  @Default private UserTestDataBuilder userBuilder = UserTestDataBuilder.builder().build();

  @Default private String email = "customer@customer.com";

  @Default private String phoneNumber = "+12345678901";

  @Default
  private OrderStatusTestDataBuilder orderStatusBuilder =
      OrderStatusTestDataBuilder.builder().build();

  @Default
  private List<OrderItemTestDataBuilder> items =
      List.of(OrderItemTestDataBuilder.builder().build());

  private RatingTestDataBuilder ratingBuilder;

  public Order buildOrder() {
    return Order.builder()
        .orderDate(LocalDateTime.now())
        .statusChangeDate(LocalDateTime.now())
        .id(id)
        .customer(userBuilder.buildUser())
        .email(email)
        .phoneNumber(phoneNumber)
        .status(orderStatusBuilder.buildOrderStatus())
        .rating(ratingBuilder != null ? ratingBuilder.buildRating() : null)
        .items(
            items.stream()
                .map(OrderItemTestDataBuilder::buildOrderItem)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll))
        .build();
  }
}
