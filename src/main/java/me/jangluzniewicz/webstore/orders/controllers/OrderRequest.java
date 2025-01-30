package me.jangluzniewicz.webstore.orders.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class OrderRequest {
    private LocalDateTime orderDate;
    private LocalDateTime statusChangeDate;
    @NotNull(message = "customerId is required")
    private Long customerId;
    private OrderStatus status;
    private RatingRequest rating;
    private List<OrderItemRequest> items;
}
