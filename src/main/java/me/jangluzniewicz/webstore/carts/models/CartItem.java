package me.jangluzniewicz.webstore.carts.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.jangluzniewicz.webstore.products.models.Product;

@Schema(description = "Model representing an item in the shopping cart")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class CartItem {
  @Schema(description = "Unique identifier of the cart item", example = "1", nullable = true)
  private Long id;

  @NonNull
  @Schema(description = "Product associated with this cart item")
  private Product product;

  @NonNull
  @Schema(description = "Quantity of the product", example = "2")
  private Integer quantity;
}
