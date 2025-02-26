package me.jangluzniewicz.webstore.orders.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Model representing a rating for an order")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Rating {
  @Schema(description = "Unique identifier of the rating", example = "1")
  private Long id;

  @Schema(description = "Rating value", example = "5")
  @NonNull
  private Integer rating;

  @Schema(description = "Description of the rating", example = "Excellent service!")
  @NonNull
  private String description;
}
