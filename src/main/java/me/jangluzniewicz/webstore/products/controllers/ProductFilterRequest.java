package me.jangluzniewicz.webstore.products.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Request payload for filtering products")
@AllArgsConstructor
@Getter
public class ProductFilterRequest {
  @Schema(description = "ID of the category", example = "2", nullable = true)
  @Min(value = 1, message = "categoryId must be a positive number")
  private Long categoryId;

  @Schema(description = "Name of the product", example = "Mountain Bike", nullable = true)
  @Size(min = 1, max = 255, message = "name must be between 1 and 255 characters")
  private String name;

  @Schema(description = "Minimum price", example = "100.00", nullable = true)
  @Min(value = 0, message = "priceFrom must be a non-negative number")
  private BigDecimal priceFrom;

  @Schema(description = "Maximum price", example = "1000.00", nullable = true)
  @Min(value = 0, message = "priceTo must be a non-negative number")
  private BigDecimal priceTo;
}
