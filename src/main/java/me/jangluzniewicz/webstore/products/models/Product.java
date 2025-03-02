package me.jangluzniewicz.webstore.products.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.*;
import me.jangluzniewicz.webstore.categories.models.Category;

@Schema(description = "Model representing a product")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Product {
  @Schema(description = "Unique identifier of the product", example = "1")
  private Long id;

  @JsonIgnore
  @Schema(description = "Image URI of the product")
  private String imageUri;

  @Schema(description = "Image URL of the product")
  private String imageUrl;

  @NonNull
  @Schema(description = "Name of the product", example = "Mountain Bike")
  private String name;

  @Schema(
      description = "Description of the product",
      example = "A durable mountain bike suitable for rough terrains",
      nullable = true)
  private String description;

  @NonNull
  @Schema(description = "Price of the product", example = "299.99")
  private BigDecimal price;

  @Schema(description = "Weight of the product", example = "15.5")
  private BigDecimal weight;

  @NonNull
  @Schema(description = "Category to which the product belongs")
  private Category category;
}
