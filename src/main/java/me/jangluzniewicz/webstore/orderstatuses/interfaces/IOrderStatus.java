package me.jangluzniewicz.webstore.orderstatuses.interfaces;

import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.orderstatuses.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.orderstatuses.models.OrderStatus;

public interface IOrderStatus {
  IdResponse createNewOrderStatus(OrderStatusRequest orderStatusRequest);

  Optional<OrderStatus> getOrderStatusById(Long id);

  PagedResponse<OrderStatus> getAllOrderStatuses(Integer page, Integer size);

  void updateOrderStatus(Long id, OrderStatusRequest orderStatusRequest);

  void deleteOrderStatus(Long id);
}
