package me.jangluzniewicz.webstore.orders.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Payload for creating or updating an order")
@AllArgsConstructor
@Getter
public class OrderRequest {
  @Schema(description = "Customer ID", example = "5")
  @NotNull(message = "customerId is required")
  @Min(value = 1, message = "customerId must be at least 1")
  private Long customerId;

  @Schema(description = "List of order items")
  @NotNull(message = "items list is required")
  @Size(min = 1, message = "items list must contain at least one item")
  private List<@Valid OrderItemRequest> items;
}
