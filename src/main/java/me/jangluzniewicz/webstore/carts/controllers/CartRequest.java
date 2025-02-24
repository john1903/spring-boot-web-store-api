package me.jangluzniewicz.webstore.carts.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CartRequest {
  @NotNull(message = "items list can be empty but not null")
  private List<@Valid CartItemRequest> items;
}
