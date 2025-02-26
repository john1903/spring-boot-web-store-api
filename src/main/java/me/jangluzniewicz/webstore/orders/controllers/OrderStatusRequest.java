package me.jangluzniewicz.webstore.orders.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Payload for updating an order's status")
@AllArgsConstructor
@Getter
public class OrderStatusRequest {
  @Schema(description = "New order status ID", example = "3")
  @NotNull(message = "orderStatusId is required")
  private Long orderStatusId;
}
