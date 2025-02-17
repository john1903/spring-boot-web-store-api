package me.jangluzniewicz.webstore.orders.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;
import me.jangluzniewicz.webstore.orders.models.Order;

public interface IOrder {
  IdResponse createNewOrder(@NotNull OrderRequest orderRequest);

  Optional<Order> getOrderById(@NotNull @Min(1) Long id);

  PagedResponse<Order> getOrdersByCustomerId(
      @NotNull @Min(1) Long customerId,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size);

  PagedResponse<Order> getAllOrders(@NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  PagedResponse<Order> getFilteredOrders(
      @NotNull OrderFilterRequest filter,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size);

  void updateOrder(@NotNull @Min(1) Long id, @NotNull OrderRequest orderRequest);

  void changeOrderStatus(@NotNull @Min(1) Long id, @NotNull OrderStatusRequest orderStatusRequest);

  void addRatingToOrder(@NotNull @Min(1) Long id, @NotNull RatingRequest ratingRequest);

  Long getOrderOwnerId(@NotNull @Min(1) Long id);

  void deleteOrder(@NotNull @Min(1) Long id);
}
