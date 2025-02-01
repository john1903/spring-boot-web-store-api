package me.jangluzniewicz.webstore.orders.interfaces;

import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.common.models.PagedResponse;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IOrder {
    Long createNewOrder(OrderRequest orderRequest);

    Optional<Order> getOrderById(Long id);

    PagedResponse<Order> getOrdersByCustomerId(Long customerId, Integer page, Integer size);

    PagedResponse<Order> getAllOrders(Integer page, Integer size);

    PagedResponse<Order> getFilteredOrders(Long statusId, LocalDateTime orderDateAfter, LocalDateTime orderDateBefore,
                                  Integer page, Integer size);

    Long updateOrder(Long id, OrderRequest orderRequest);

    Long changeOrderStatus(Long id, ChangeOrderStatusRequest changeOrderStatusRequest);

    Long addRatingToOrder(Long id, RatingRequest ratingRequest);

    void deleteOrder(Long id);
}
