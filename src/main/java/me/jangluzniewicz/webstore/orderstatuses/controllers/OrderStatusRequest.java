package me.jangluzniewicz.webstore.orderstatuses.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Payload for creating or updating an order status")
@AllArgsConstructor
@Getter
public class OrderStatusRequest {
  @Schema(description = "Name of the order status", example = "PENDING")
  @NotNull(message = "Name is required")
  @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
  private String name;
}
