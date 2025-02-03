package me.jangluzniewicz.webstore.orders.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangeOrderStatusRequest {
  @NotNull(message = "orderStatusId is required")
  private Long orderStatusId;
}
