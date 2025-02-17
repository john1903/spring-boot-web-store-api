package me.jangluzniewicz.webstore.orders.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderRequest {
  @NotNull(message = "customerId is required")
  private Long customerId;

  @NotNull(message = "items list is required")
  @Size(min = 1, message = "items list can be empty but not null")
  private List<@Valid OrderItemRequest> items;
}
