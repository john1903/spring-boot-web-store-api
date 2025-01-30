package me.jangluzniewicz.webstore.orders.services;

import jakarta.transaction.Transactional;
import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.interfaces.IOrder;
import me.jangluzniewicz.webstore.orders.models.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements IOrder {
    @Override
    @Transactional
    public Long createNewOrder(OrderRequest orderRequest) {
        return 0L;
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return Optional.empty();
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId, Integer page, Integer size) {
        return List.of();
    }

    @Override
    public List<Order> getAllOrders(Integer page, Integer size) {
        return List.of();
    }

    @Override
    public List<Order> getFilteredOrders(Long statusId, LocalDateTime orderDateAfter, LocalDateTime orderDateBefore, Integer page, Integer size) {
        return List.of();
    }

    @Override
    @Transactional
    public Long updateOrder(Long orderId, OrderRequest orderRequest) {
        return 0L;
    }

    @Override
    @Transactional
    public Long changeOrderStatus(Long orderId, ChangeOrderStatusRequest changeOrderStatusRequest) {
        return 0L;
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {

    }
}
