package me.jangluzniewicz.webstore.orders.interfaces;

import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.models.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IOrder {
    Long createNewOrder(OrderRequest orderRequest);

    Optional<Order> getOrderById(Long orderId);

    List<Order> getOrdersByCustomerId(Long customerId, Integer page, Integer size);

    List<Order> getAllOrders(Integer page, Integer size);

    List<Order> getFilteredOrders(Long statusId, LocalDateTime orderDateAfter, LocalDateTime orderDateBefore,
                                  Integer page, Integer size);

    Long updateOrder(Long orderId, OrderRequest orderRequest);

    Long changeOrderStatus(Long orderId, ChangeOrderStatusRequest changeOrderStatusRequest);

    void deleteOrder(Long orderId);
}
