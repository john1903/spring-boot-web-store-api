package me.jangluzniewicz.webstore.common.testdata.orders;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.models.OrderItem;
import me.jangluzniewicz.webstore.common.testdata.products.ProductTestDataBuilder;

@Builder
public class OrderItemTestDataBuilder {
  private Long id;
  @Default private BigDecimal discount = BigDecimal.ONE;
  @Default private int quantity = 1;
  @Default private ProductTestDataBuilder productBuilder = ProductTestDataBuilder.builder().build();

  public OrderItem buildOrderItem() {
    var product = productBuilder.buildProduct();
    return OrderItem.builder()
        .id(id)
        .discount(discount)
        .quantity(quantity)
        .product(product)
        .price(product.getPrice())
        .build();
  }
}
