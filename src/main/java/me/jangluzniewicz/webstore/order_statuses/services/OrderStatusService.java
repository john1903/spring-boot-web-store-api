package me.jangluzniewicz.webstore.order_statuses.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.order_statuses.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.order_statuses.mappers.OrderStatusMapper;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.order_statuses.repositories.OrderStatusRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class OrderStatusService implements IOrderStatus {
    private final OrderStatusRepository orderStatusRepository;
    private final OrderStatusMapper orderStatusMapper;

    public OrderStatusService(OrderStatusRepository orderStatusRepository, OrderStatusMapper orderStatusMapper) {
        this.orderStatusRepository = orderStatusRepository;
        this.orderStatusMapper = orderStatusMapper;
    }

    @Override
    @Transactional
    public Long createNewOrderStatus(@NotNull OrderStatusRequest orderStatusRequest) {
        if (orderStatusRepository.existsByName(orderStatusRequest.getName())) {
            throw new NotUniqueException("Order status with name " + orderStatusRequest.getName() + " already exists");
        }
        OrderStatus orderStatus = OrderStatus.builder()
                .name(orderStatusRequest.getName())
                .build();
        return orderStatusRepository.save(orderStatusMapper.toEntity(orderStatus)).getId();
    }

    @Override
    public Optional<OrderStatus> getOrderStatusById(@Min(1) Long id) {
        return orderStatusRepository.findById(id)
                .map(orderStatusMapper::fromEntity);
    }

    @Override
    public List<OrderStatus> getAllOrderStatuses(@Min(1) Integer page, @Min(1) Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderStatus> orderStatuses = orderStatusRepository.findAll(pageable).map(orderStatusMapper::fromEntity);
        return orderStatuses.toList();
    }

    @Override
    @Transactional
    public Long updateOrderStatus(@Min(1) Long id, @NotNull OrderStatusRequest orderStatusRequest) {
        OrderStatusEntity orderStatusEntity = orderStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order status with id " + id + " not found"));
        if (orderStatusRepository.existsByName(orderStatusRequest.getName()) &&
                !orderStatusEntity.getName().equals(orderStatusRequest.getName())) {
            throw new NotUniqueException("Order status with name " + orderStatusRequest.getName() + " already exists");
        }
        orderStatusEntity.setName(orderStatusRequest.getName());
        return orderStatusEntity.getId();
    }

    @Override
    @Transactional
    public void deleteOrderStatus(@Min(1) Long id) {
        OrderStatusEntity orderStatusEntity = orderStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order status with id " + id + " not found"));
        try {
            orderStatusRepository.delete(orderStatusEntity);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new DeletionNotAllowedException("Order status with id " + id +
                        " cannot be deleted due to existing relations");
            }
        }
    }
}
