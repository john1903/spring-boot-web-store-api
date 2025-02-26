package me.jangluzniewicz.webstore.orders.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Payload for rating an order")
@AllArgsConstructor
@Getter
public class RatingRequest {
  @Schema(description = "Rating ID", example = "1", nullable = true)
  private Long id;

  @Schema(description = "Rating value", example = "4")
  @NotNull(message = "rating is required")
  @Min(value = 1, message = "rating must be at least 1")
  @Max(value = 5, message = "rating must be at most 5")
  private Integer rating;

  @Schema(description = "Rating description", example = "Great service!")
  @NotNull(message = "description is required")
  @Size(min = 1, max = 500, message = "description must be between 1 and 500 characters")
  private String description;
}
