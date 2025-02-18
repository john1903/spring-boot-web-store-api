package me.jangluzniewicz.webstore.commons.testdata.products;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;

@Builder
public class ProductFilterRequestTestDataBuilder {
  @Default private Long categoryId = 1L;
  @Default private String name = "Bicycle";
  @Default private BigDecimal priceFrom = BigDecimal.valueOf(0);
  @Default private BigDecimal priceTo = BigDecimal.valueOf(2000.0);

  public ProductFilterRequest buildProductFilterRequest() {
    return new ProductFilterRequest(categoryId, name, priceFrom, priceTo);
  }
}
