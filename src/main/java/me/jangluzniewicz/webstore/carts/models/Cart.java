package me.jangluzniewicz.webstore.carts.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Schema(description = "Model representing a shopping cart")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Cart {
  @Schema(description = "Unique identifier of the cart", example = "1", nullable = true)
  private Long id;

  @NonNull
  @Schema(description = "Identifier of the customer owning the cart", example = "2")
  private Long customerId;

  @NonNull
  @Schema(description = "List of items in the cart")
  private List<CartItem> items;

  @Schema(description = "Total price of the cart", example = "599.98", nullable = true)
  private BigDecimal total;
}
