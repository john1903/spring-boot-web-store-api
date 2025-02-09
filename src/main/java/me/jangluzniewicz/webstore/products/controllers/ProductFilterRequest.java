package me.jangluzniewicz.webstore.products.controllers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductFilterRequest {
  @Min(value = 1, message = "categoryId must be a positive number")
  private Long categoryId;

  @Size(min = 1, max = 255, message = "name must be between 1 and 255 characters")
  private String name;

  @Min(value = 0, message = "priceFrom must be a non-negative number")
  private BigDecimal priceFrom;

  @Min(value = 0, message = "priceTo must be a non-negative number")
  private BigDecimal priceTo;
}
