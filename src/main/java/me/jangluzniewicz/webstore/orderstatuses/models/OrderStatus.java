package me.jangluzniewicz.webstore.orderstatuses.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Model representing an order status")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class OrderStatus {
  @Schema(description = "Unique identifier of the order status", example = "1")
  private Long id;

  @NonNull
  @Schema(description = "Name of the order status", example = "PENDING")
  private String name;
}
