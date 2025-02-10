package me.jangluzniewicz.webstore.utils.products;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.utils.categories.CategoryTestDataBuilder;

@Builder
public class ProductTestDataBuilder {
  private Long id;
  @Default private String name = "Bicycle";
  @Default private String description = "Mountain bike";
  @Default private BigDecimal price = BigDecimal.valueOf(1000.0);
  @Default private BigDecimal weight = BigDecimal.valueOf(10.0);

  @Default
  private CategoryTestDataBuilder categoryBuilder = CategoryTestDataBuilder.builder().build();

  public Product buildProduct() {
    return Product.builder()
        .id(id)
        .name(name)
        .description(description)
        .price(price)
        .weight(weight)
        .category(categoryBuilder.buildCategory())
        .build();
  }
}
