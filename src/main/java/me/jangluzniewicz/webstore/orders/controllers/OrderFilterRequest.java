package me.jangluzniewicz.webstore.orders.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Filter criteria for searching orders")
@AllArgsConstructor
@Getter
public class OrderFilterRequest {
  @Schema(description = "Filter orders by status ID", example = "2")
  @Min(value = 1, message = "customerId must be a positive number")
  Long statusId;

  @Schema(
      description = "Filter orders placed after this date-time",
      example = "2025-01-01T00:00:00")
  LocalDateTime orderDateAfter;

  @Schema(
      description = "Filter orders placed before this date-time",
      example = "2025-12-31T23:59:59")
  LocalDateTime orderDateBefore;
}
