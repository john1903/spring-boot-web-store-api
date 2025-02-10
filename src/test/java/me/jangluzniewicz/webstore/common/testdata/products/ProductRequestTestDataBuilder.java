package me.jangluzniewicz.webstore.common.testdata.products;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;

@Builder
public class ProductRequestTestDataBuilder {
  @Default private String name = "Bicycle";
  @Default private String description = "Mountain bike";
  @Default private BigDecimal price = BigDecimal.valueOf(1000.0);
  @Default private BigDecimal weight = BigDecimal.valueOf(10.0);
  private Long categoryId;

  public ProductRequest buildProductRequest() {
    return new ProductRequest(name, description, price, weight, categoryId);
  }
}
