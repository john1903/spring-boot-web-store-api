package me.jangluzniewicz.webstore.orders.controllers;

import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderFilterRequest {
  @Min(value = 1, message = "customerId must be a positive number")
  Long statusId;

  LocalDateTime orderDateAfter;
  LocalDateTime orderDateBefore;
}
