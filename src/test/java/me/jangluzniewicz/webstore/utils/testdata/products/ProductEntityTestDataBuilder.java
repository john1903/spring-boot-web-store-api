package me.jangluzniewicz.webstore.utils.testdata.products;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Builder.Default;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.utils.testdata.categories.CategoryEntityTestDataBuilder;

@Builder
public class ProductEntityTestDataBuilder {
  @Default private Long id = 1L;
  @Default private String name = "Bicycle";
  @Default private String description = "Mountain bike";
  @Default private BigDecimal price = BigDecimal.valueOf(1000.0);
  @Default private BigDecimal weight = BigDecimal.valueOf(10.0);

  @Default
  private CategoryEntityTestDataBuilder categoryBuilder =
      CategoryEntityTestDataBuilder.builder().build();

  public ProductEntity buildProductEntity() {
    return ProductEntity.builder()
        .id(id)
        .name(name)
        .description(description)
        .price(price)
        .weight(weight)
        .category(categoryBuilder.buildCategoryEntity())
        .build();
  }
}
