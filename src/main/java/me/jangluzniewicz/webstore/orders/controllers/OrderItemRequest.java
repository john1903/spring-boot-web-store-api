package me.jangluzniewicz.webstore.orders.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Payload for creating or updating an order item")
@AllArgsConstructor
@Getter
public class OrderItemRequest {
  @Schema(description = "Order item ID", example = "1", nullable = true)
  @Min(value = 1, message = "id must be at least 1")
  private Long id;

  @Schema(description = "Product ID", example = "10")
  @NotNull(message = "productId is required")
  @Min(value = 1, message = "productId must be at least 1")
  private Long productId;

  @Schema(description = "Quantity of the product", example = "2")
  @NotNull(message = "quantity is required")
  @Min(value = 1, message = "quantity must be at least 1")
  @Max(value = 100, message = "quantity must be at most 100")
  private Integer quantity;
}
