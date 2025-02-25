package me.jangluzniewicz.webstore.utils.testdata.products;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;

@Builder
public class ProductFilterRequestTestDataBuilder {
  @Default private Long categoryId = 1L;
  @Default private String name = "phones";
  @Default private BigDecimal priceFrom = BigDecimal.valueOf(0.0);
  @Default private BigDecimal priceTo = BigDecimal.valueOf(100.0);

  public ProductFilterRequest buildProductFilterRequest() {
    return new ProductFilterRequest(categoryId, name, priceFrom, priceTo);
  }

  public String toRequestParams() {
    return String.format(
        "categoryId=%d&name=%s&priceFrom=%s&priceTo=%s", categoryId, name, priceFrom, priceTo);
  }
}
