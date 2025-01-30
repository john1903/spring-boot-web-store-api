package me.jangluzniewicz.webstore.carts.controllers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CartItemRequest {
    @NotNull(message = "productId is required")
    private Long productId;
    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    @Max(value = 100, message = "quantity must be at most 100")
    private Integer quantity;
}
