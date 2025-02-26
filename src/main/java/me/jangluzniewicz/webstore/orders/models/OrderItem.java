package me.jangluzniewicz.webstore.orders.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.*;
import me.jangluzniewicz.webstore.products.models.Product;

@Schema(description = "Model representing an order item")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class OrderItem {
  @Schema(description = "Unique identifier of the order item", example = "1")
  private Long id;

  @Schema(description = "Product associated with the order item")
  @NonNull
  private Product product;

  @Schema(description = "Quantity of the product ordered", example = "2")
  @NonNull
  private Integer quantity;

  @Schema(description = "Price of the product at the time of order", example = "299.99")
  @NonNull
  private BigDecimal price;
}
