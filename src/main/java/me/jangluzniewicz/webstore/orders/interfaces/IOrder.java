package me.jangluzniewicz.webstore.orders.interfaces;

import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;
import me.jangluzniewicz.webstore.orders.models.Order;

public interface IOrder {
  IdResponse createNewOrder(OrderRequest orderRequest);

  Optional<Order> getOrderById(Long id);

  PagedResponse<Order> getOrdersByCustomerId(Long customerId, Integer page, Integer size);

  PagedResponse<Order> getAllOrders(Integer page, Integer size);

  PagedResponse<Order> getFilteredOrders(OrderFilterRequest filter, Integer page, Integer size);

  void updateOrder(Long id, OrderRequest orderRequest);

  void changeOrderStatus(Long id, ChangeOrderStatusRequest changeOrderStatusRequest);

  void addRatingToOrder(Long id, RatingRequest ratingRequest);

  Long getOrderOwnerId(Long id);

  void deleteOrder(Long id);
}
