package me.jangluzniewicz.webstore.products.models;

import java.math.BigDecimal;
import lombok.*;
import me.jangluzniewicz.webstore.categories.models.Category;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Product {
  private Long id;
  @NonNull private String name;
  private String description;
  @NonNull private BigDecimal price;
  private BigDecimal weight;
  @NonNull private Category category;
}
