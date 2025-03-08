package me.jangluzniewicz.webstore.utils.testdata.orders;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;

@Builder
public class OrderRequestTestDataBuilder {
  @Default private Long customerId = 2L;

  @Default private String email = "customer@customer.com";

  @Default private String phoneNumber = "+12345678901";

  @Default
  private List<OrderItemRequestTestDataBuilder> items =
      List.of(OrderItemRequestTestDataBuilder.builder().build());

  public OrderRequest buildOrderRequest() {
    return new OrderRequest(
        customerId,
        email,
        phoneNumber,
        items.stream()
            .map(OrderItemRequestTestDataBuilder::buildOrderItemRequest)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
  }

  public String toJson() {
    return String.format(
        "{\"customerId\":%d,\"email\":\"%s\",\"phoneNumber\":\"%s\",\"items\":[%s]}",
        customerId,
        email,
        phoneNumber,
        items.stream()
            .map(OrderItemRequestTestDataBuilder::toJson)
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append));
  }
}
