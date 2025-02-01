package me.jangluzniewicz.webstore.order_statuses.interfaces;

import me.jangluzniewicz.webstore.order_statuses.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.common.models.PagedResponse;

import java.util.Optional;

public interface IOrderStatus {
    Long createNewOrderStatus(OrderStatusRequest orderStatusRequest);

    Optional<OrderStatus> getOrderStatusById(Long id);

    PagedResponse<OrderStatus> getAllOrderStatuses(Integer page, Integer size);

    Long updateOrderStatus(Long id, OrderStatusRequest orderStatusRequest);

    void deleteOrderStatus(Long id);
}
