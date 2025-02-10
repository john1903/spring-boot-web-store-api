package me.jangluzniewicz.webstore.order_statuses.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.order_statuses.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.order_statuses.mappers.OrderStatusMapper;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.order_statuses.repositories.OrderStatusRepository;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusService implements IOrderStatus {
  private final OrderStatusRepository orderStatusRepository;
  private final OrderStatusMapper orderStatusMapper;

  public OrderStatusService(
      OrderStatusRepository orderStatusRepository, OrderStatusMapper orderStatusMapper) {
    this.orderStatusRepository = orderStatusRepository;
    this.orderStatusMapper = orderStatusMapper;
  }

  @Override
  @Transactional
  public Long createNewOrderStatus(@NotNull OrderStatusRequest orderStatusRequest) {
    if (orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest.getName())) {
      throw new NotUniqueException(
          "Order status with name " + orderStatusRequest.getName() + " already exists");
    }
    OrderStatus orderStatus = OrderStatus.builder().name(orderStatusRequest.getName()).build();
    return orderStatusRepository.save(orderStatusMapper.toEntity(orderStatus)).getId();
  }

  @Override
  public Optional<OrderStatus> getOrderStatusById(@NotNull @Min(1) Long id) {
    return orderStatusRepository.findById(id).map(orderStatusMapper::fromEntity);
  }

  @Override
  public PagedResponse<OrderStatus> getAllOrderStatuses(
      @NotNull @Min(0) Integer page, @Min(1) Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<OrderStatus> orderStatuses =
        orderStatusRepository.findAll(pageable).map(orderStatusMapper::fromEntity);
    return new PagedResponse<>(orderStatuses.getTotalPages(), orderStatuses.toList());
  }

  @Override
  @Transactional
  public Long updateOrderStatus(
      @NotNull @Min(1) Long id, @NotNull OrderStatusRequest orderStatusRequest) {
    OrderStatus orderStatus =
        getOrderStatusById(id)
            .orElseThrow(() -> new NotFoundException("Order status with id " + id + " not found"));
    if (orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest.getName())
        && !orderStatus.getName().equals(orderStatusRequest.getName())) {
      throw new NotUniqueException(
          "Order status with name " + orderStatusRequest.getName() + " already exists");
    }
    orderStatus.setName(orderStatusRequest.getName());
    return orderStatusRepository.save(orderStatusMapper.toEntity(orderStatus)).getId();
  }

  @Override
  @Transactional
  public void deleteOrderStatus(@NotNull @Min(1) Long id) {
    if (!orderStatusRepository.existsById(id)) {
      throw new NotFoundException("Order status with id " + id + " not found");
    }
    try {
      orderStatusRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException) {
        throw new DeletionNotAllowedException(
            "Order status with id " + id + " cannot be deleted due to existing relations");
      } else {
        throw e;
      }
    }
  }
}
