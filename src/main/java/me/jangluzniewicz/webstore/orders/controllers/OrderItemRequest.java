package me.jangluzniewicz.webstore.orders.controllers;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class OrderItemRequest {
    private Long id;
    @NotNull(message = "productId is required")
    private Long productId;
    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    @Max(value = 100, message = "quantity must be at most 100")
    private Integer quantity;
    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", message = "price cannot be negative")
    private BigDecimal price;
    @DecimalMin(value = "0.0", message = "discount cannot be negative")
    @DecimalMax(value = "1.0", message = "discount must be at most 1.0")
    private BigDecimal discount;
}
