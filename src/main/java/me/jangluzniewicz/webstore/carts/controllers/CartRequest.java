package me.jangluzniewicz.webstore.carts.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Payload for updating the entire cart")
@AllArgsConstructor
@Getter
public class CartRequest {
  @Schema(description = "List of cart items; can be empty but not null")
  @NotNull(message = "items list can be empty but not null")
  private List<@Valid CartItemRequest> items;
}
