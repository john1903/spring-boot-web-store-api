package me.jangluzniewicz.webstore.common.testdata.orders;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.common.testdata.products.ProductTestDataBuilder;
import me.jangluzniewicz.webstore.orders.models.OrderItem;

@Builder
public class OrderItemTestDataBuilder {
  @Default private Long id = 1L;
  @Default private BigDecimal discount = BigDecimal.ONE;
  @Default private int quantity = 1;
  @Default private ProductTestDataBuilder productBuilder = ProductTestDataBuilder.builder().build();

  public OrderItem buildOrderItem() {
    var product = productBuilder.buildProduct();
    return OrderItem.builder()
        .id(id)
        .quantity(quantity)
        .product(product)
        .price(product.getPrice())
        .build();
  }
}
