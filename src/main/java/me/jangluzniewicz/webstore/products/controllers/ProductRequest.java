package me.jangluzniewicz.webstore.products.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Payload for creating or updating a product")
@AllArgsConstructor
@Getter
public class ProductRequest {
  @Schema(description = "Name of the product", example = "Mountain Bike")
  @NotNull(message = "name is required")
  @Size(min = 1, max = 255, message = "name must be between 1 and 255 characters")
  private String name;

  @Schema(
      description = "Description of the product",
      example = "A durable mountain bike suitable for rough terrains",
      nullable = true)
  @Size(max = 5000, message = "description must be at most 5000 characters")
  private String description;

  @Schema(description = "Price of the product", example = "299.99")
  @NotNull(message = "price is required")
  @DecimalMin(value = "0.0", message = "price cannot be negative")
  private BigDecimal price;

  @Schema(description = "Weight of the product", example = "15.5", nullable = true)
  @DecimalMin(value = "0.0", message = "weight cannot be negative")
  private BigDecimal weight;

  @Schema(description = "Category ID to which the product belongs", example = "2")
  @NotNull(message = "categoryId is required")
  private Long categoryId;
}
