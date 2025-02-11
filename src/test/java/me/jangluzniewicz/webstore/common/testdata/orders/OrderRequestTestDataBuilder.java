package me.jangluzniewicz.webstore.common.testdata.orders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;

@Builder
public class OrderRequestTestDataBuilder {
  @Default private LocalDateTime orderDate = LocalDateTime.now();
  @Default private LocalDateTime statusChangeDate = LocalDateTime.now();
  private Long customerId;
  private Long statusId;

  private RatingRequestTestDataBuilder ratingBuilder;

  @Default
  private List<OrderItemRequestTestDataBuilder> items =
      List.of(OrderItemRequestTestDataBuilder.builder().build());

  public OrderRequest buildOrderRequest() {
    return new OrderRequest(
        orderDate,
        statusChangeDate,
        customerId,
        statusId,
        ratingBuilder != null ? ratingBuilder.buildRatingRequest() : null,
        items.stream()
            .map(OrderItemRequestTestDataBuilder::buildOrderItemRequest)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
  }
}
