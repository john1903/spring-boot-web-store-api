package me.jangluzniewicz.webstore.common.testdata.orders;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.common.testdata.products.ProductEntityTestDataBuilder;
import me.jangluzniewicz.webstore.orders.entities.OrderItemEntity;

@Builder
public class OrderItemEntityTestDataBuilder {
  @Default private Long id = 1L;
  @Default private BigDecimal discount = BigDecimal.ONE;
  @Default private int quantity = 1;

  @Default
  private ProductEntityTestDataBuilder productBuilder =
      ProductEntityTestDataBuilder.builder().build();

  public OrderItemEntity buildOrderItemEntity() {
    var product = productBuilder.buildProductEntity();
    return OrderItemEntity.builder()
        .id(id)
        .discount(discount)
        .quantity(quantity)
        .product(product)
        .price(product.getPrice())
        .build();
  }
}
