package me.jangluzniewicz.webstore.orders.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderRequest {
  private LocalDateTime orderDate;
  private LocalDateTime statusChangeDate;

  @NotNull(message = "customerId is required")
  private Long customerId;

  private Long statusId;
  @Valid private RatingRequest rating;

  @NotNull(message = "items list can be empty but not null")
  private List<@Valid OrderItemRequest> items;
}
