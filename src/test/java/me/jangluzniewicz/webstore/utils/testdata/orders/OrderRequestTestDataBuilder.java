package me.jangluzniewicz.webstore.utils.testdata.orders;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;

@Builder
public class OrderRequestTestDataBuilder {
  @Default private Long customerId = 1L;

  @Default
  private List<OrderItemRequestTestDataBuilder> items =
      List.of(OrderItemRequestTestDataBuilder.builder().build());

  public OrderRequest buildOrderRequest() {
    return new OrderRequest(
        customerId,
        items.stream()
            .map(OrderItemRequestTestDataBuilder::buildOrderItemRequest)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
  }

  public String toJson() {
    return String.format(
        "{\"customerId\":%d,\"items\":[%s]}",
        customerId,
        items.stream()
            .map(OrderItemRequestTestDataBuilder::toJson)
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append));
  }
}
