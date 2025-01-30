package me.jangluzniewicz.webstore.carts.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CartRequest {
    @NotNull(message = "customerId is required")
    private Long customerId;
    @NotNull(message = "items list can be empty but not null")
    private List<CartItemRequest> items;
}
