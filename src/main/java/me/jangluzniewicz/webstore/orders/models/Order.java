package me.jangluzniewicz.webstore.orders.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import me.jangluzniewicz.webstore.orderstatuses.models.OrderStatus;
import me.jangluzniewicz.webstore.users.models.User;

@Schema(description = "Model representing an order")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Order {
  @Schema(description = "Unique identifier of the order", example = "1")
  private Long id;

  @Schema(description = "Date and time when the order was placed", example = "2025-03-15T10:00:00")
  private LocalDateTime orderDate;

  @Schema(
      description = "Date and time when the order status was last changed",
      example = "2025-03-15T12:00:00")
  private LocalDateTime statusChangeDate;

  @Schema(description = "Customer who placed the order")
  @NonNull
  private User customer;

  @Schema(description = "Email address of the user", example = "customer@customer.com")
  private String email;

  @Schema(description = "Phone number of the user", example = "+12345678901")
  private String phoneNumber;

  @Schema(description = "Current status of the order")
  private OrderStatus status;

  @Schema(description = "Rating given to the order")
  private Rating rating;

  @Schema(description = "List of order items")
  private List<OrderItem> items;

  @Schema(description = "Total amount of the order", example = "599.98")
  private BigDecimal total;
}
