package me.jangluzniewicz.webstore.products.controllers;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class ProductRequest {
    @NotNull(message = "name is required")
    @Size(min = 1, max = 255, message = "name must be between 1 and 255 characters")
    private String name;
    @Size(max = 5000, message = "description must be at most 5000 characters")
    private String description;
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "price cannot be negative")
    private BigDecimal price;
    @DecimalMin(value = "0.0", message = "weight cannot be negative")
    private BigDecimal weight;
    @NotNull(message = "categoryId is required")
    private Long categoryId;
}
