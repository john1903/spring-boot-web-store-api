package me.jangluzniewicz.webstore.utils.testdata.products;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;

@Builder
public class ProductRequestTestDataBuilder {
  @Default private String name = "Headphones";
  @Default private String description = "Wireless headphones";
  @Default private BigDecimal price = BigDecimal.valueOf(99.99);
  @Default private BigDecimal weight = BigDecimal.valueOf(0.5);
  @Default private Long categoryId = 1L;

  public ProductRequest buildProductRequest() {
    return new ProductRequest(name, description, price, weight, categoryId);
  }

  public String toJson() {
    return """
        {
          "name": "%s",
          "description": "%s",
          "price": %s,
          "weight": %s,
          "categoryId": %d
        }
        """
        .formatted(name, description, price, weight, categoryId);
  }
}
