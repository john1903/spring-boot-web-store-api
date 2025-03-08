package me.jangluzniewicz.webstore.orders.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.jangluzniewicz.webstore.orders.validators.CustomerDetailsRequired;

@Schema(description = "Payload for creating or updating an order")
@CustomerDetailsRequired
@AllArgsConstructor
@Getter
public class OrderRequest {
  @Schema(description = "Customer ID", example = "5", nullable = true)
  @Min(value = 1, message = "customerId must be at least 1")
  private Long customerId;

  @Schema(
      description = "Email address of the user",
      example = "customer@customer.com",
      nullable = true)
  @Size(min = 5, max = 255, message = "email must be between 5 and 255 characters")
  @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email format")
  private String email;

  @Schema(description = "Phone number of the user", example = "+12345678901", nullable = true)
  @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phoneNumber format")
  private String phoneNumber;

  @Schema(description = "List of order items")
  @NotNull(message = "items list is required")
  @Size(min = 1, message = "items list must contain at least one item")
  private List<@Valid OrderItemRequest> items;
}
