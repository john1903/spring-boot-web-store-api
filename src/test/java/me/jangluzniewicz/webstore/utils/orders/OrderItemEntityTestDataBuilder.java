package me.jangluzniewicz.webstore.utils.orders;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.entities.OrderItemEntity;
import me.jangluzniewicz.webstore.utils.products.ProductEntityTestDataBuilder;

@Builder
public class OrderItemEntityTestDataBuilder {
  private Long id;
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
