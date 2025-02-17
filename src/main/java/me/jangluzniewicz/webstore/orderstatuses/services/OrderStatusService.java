package me.jangluzniewicz.webstore.orderstatuses.services;

import jakarta.transaction.Transactional;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.orderstatuses.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.orderstatuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.orderstatuses.mappers.OrderStatusMapper;
import me.jangluzniewicz.webstore.orderstatuses.models.OrderStatus;
import me.jangluzniewicz.webstore.orderstatuses.repositories.OrderStatusRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
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
  public IdResponse createNewOrderStatus(OrderStatusRequest orderStatusRequest) {
    if (orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest.getName())) {
      throw new NotUniqueException(
          "Order status with name " + orderStatusRequest.getName() + " already exists");
    }
    OrderStatus orderStatus = OrderStatus.builder().name(orderStatusRequest.getName()).build();
    return new IdResponse(
        orderStatusRepository.save(orderStatusMapper.toEntity(orderStatus)).getId());
  }

  @Override
  public Optional<OrderStatus> getOrderStatusById(Long id) {
    return orderStatusRepository.findById(id).map(orderStatusMapper::fromEntity);
  }

  @Override
  public PagedResponse<OrderStatus> getAllOrderStatuses(Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<OrderStatus> orderStatuses =
        orderStatusRepository.findAll(pageable).map(orderStatusMapper::fromEntity);
    return new PagedResponse<>(orderStatuses.getTotalPages(), orderStatuses.toList());
  }

  @Override
  @Transactional
  public void updateOrderStatus(Long id, OrderStatusRequest orderStatusRequest) {
    OrderStatus orderStatus =
        getOrderStatusById(id)
            .orElseThrow(() -> new NotFoundException("Order status with id " + id + " not found"));
    if (orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest.getName())
        && !orderStatus.getName().equals(orderStatusRequest.getName())) {
      throw new NotUniqueException(
          "Order status with name " + orderStatusRequest.getName() + " already exists");
    }
    orderStatus.setName(orderStatusRequest.getName());
    orderStatusRepository.save(orderStatusMapper.toEntity(orderStatus));
  }

  @Override
  @Transactional
  public void deleteOrderStatus(Long id) {
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
