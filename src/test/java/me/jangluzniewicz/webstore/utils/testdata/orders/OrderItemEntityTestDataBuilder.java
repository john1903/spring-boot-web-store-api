package me.jangluzniewicz.webstore.utils.testdata.orders;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.orders.entities.OrderItemEntity;
import me.jangluzniewicz.webstore.utils.testdata.products.ProductEntityTestDataBuilder;

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
        .quantity(quantity)
        .product(product)
        .price(product.getPrice())
        .build();
  }
}
