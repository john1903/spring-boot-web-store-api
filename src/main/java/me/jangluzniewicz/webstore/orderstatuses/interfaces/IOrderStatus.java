package me.jangluzniewicz.webstore.orderstatuses.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.orderstatuses.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.orderstatuses.models.OrderStatus;

public interface IOrderStatus {
  IdResponse createNewOrderStatus(@NotNull OrderStatusRequest orderStatusRequest);

  Optional<OrderStatus> getOrderStatusById(@NotNull @Min(1) Long id);

  PagedResponse<OrderStatus> getAllOrderStatuses(
      @NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  void updateOrderStatus(@NotNull @Min(1) Long id, @NotNull OrderStatusRequest orderStatusRequest);

  void deleteOrderStatus(@NotNull @Min(1) Long id);
}
