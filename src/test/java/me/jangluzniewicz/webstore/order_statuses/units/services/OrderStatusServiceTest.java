package me.jangluzniewicz.webstore.order_statuses.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.order_statuses.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.mappers.OrderStatusMapper;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.order_statuses.repositories.OrderStatusRepository;
import me.jangluzniewicz.webstore.order_statuses.services.OrderStatusService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest {
  @Mock private OrderStatusRepository orderStatusRepository;
  @Mock private OrderStatusMapper orderStatusMapper;
  @InjectMocks private OrderStatusService orderStatusService;

  @Test
  public void shouldCreateNewOrderStatusAndReturnOrderStatusId() {
    OrderStatusRequest orderStatusRequest = new OrderStatusRequest("ACCEPTED");
    OrderStatusEntity savedEntity = new OrderStatusEntity(1L, "ACCEPTED");

    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest.getName()))
        .thenReturn(false);
    when(orderStatusMapper.toEntity(any())).thenReturn(new OrderStatusEntity(null, "ACCEPTED"));
    when(orderStatusRepository.save(any())).thenReturn(savedEntity);

    Long orderStatusId = orderStatusService.createNewOrderStatus(orderStatusRequest);

    assertEquals(1L, orderStatusId);
  }

  @Test
  public void shouldThrowNotUniqueExceptionWhenOrderStatusAlreadyExists() {
    OrderStatusRequest orderStatusRequest = new OrderStatusRequest("ACCEPTED");

    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest.getName()))
        .thenReturn(true);

    assertThrows(
        Exception.class, () -> orderStatusService.createNewOrderStatus(orderStatusRequest));
  }

  @Test
  public void shouldReturnOrderStatusWhenGettingOrderStatusById() {
    OrderStatusEntity orderStatusEntity = new OrderStatusEntity(1L, "ACCEPTED");

    when(orderStatusRepository.findById(1L)).thenReturn(java.util.Optional.of(orderStatusEntity));
    when(orderStatusMapper.fromEntity(orderStatusEntity))
        .thenReturn(new OrderStatus(1L, "ACCEPTED"));

    Optional<OrderStatus> orderStatus = orderStatusService.getOrderStatusById(1L);

    assertTrue(orderStatus.isPresent());
    assertEquals(1L, orderStatus.get().getId());
    assertEquals("ACCEPTED", orderStatus.get().getName());
  }

  @Test
  public void shouldReturnEmptyWhenOrderStatusNotFoundById() {
    when(orderStatusRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<OrderStatus> orderStatus = orderStatusService.getOrderStatusById(1L);

    assertTrue(orderStatus.isEmpty());
  }

  @Test
  public void shouldReturnPagedResponseWhenGettingAllOrderStatuses() {
    OrderStatusEntity orderStatusEntity = new OrderStatusEntity(1L, "ACCEPTED");
    Pageable pageable = PageRequest.of(0, 10);
    Page<OrderStatusEntity> page = new PageImpl<>(List.of(orderStatusEntity), pageable, 1);

    when(orderStatusRepository.findAll(pageable)).thenReturn(page);
    when(orderStatusMapper.fromEntity(orderStatusEntity))
        .thenReturn(new OrderStatus(1L, "ACCEPTED"));

    PagedResponse<OrderStatus> pagedResponse = orderStatusService.getAllOrderStatuses(0, 10);

    assertEquals(1, pagedResponse.getTotalPages());
    assertEquals(1, pagedResponse.getContent().size());
    assertEquals(1L, pagedResponse.getContent().getFirst().getId());
    assertEquals("ACCEPTED", pagedResponse.getContent().getFirst().getName());
  }

  @Test
  public void shouldUpdateOrderStatusAndReturnOrderStatusId() {
    OrderStatusRequest orderStatusRequest = new OrderStatusRequest("DELIVERED");
    OrderStatusEntity savedEntity = new OrderStatusEntity(1L, "ACCEPTED");

    when(orderStatusRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest.getName()))
        .thenReturn(false);

    Long orderStatusId = orderStatusService.updateOrderStatus(1L, orderStatusRequest);

    assertEquals(1L, orderStatusId);
  }

  @Test
  public void shouldThrowNotUniqueExceptionWhenOrderStatusAlreadyExistsOnUpdate() {
    OrderStatusRequest orderStatusRequest = new OrderStatusRequest("DELIVERED");
    OrderStatusEntity savedEntity = new OrderStatusEntity(1L, "ACCEPTED");

    when(orderStatusRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest.getName()))
        .thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () -> orderStatusService.updateOrderStatus(1L, orderStatusRequest));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenOrderStatusNotFoundOnUpdate() {
    OrderStatusRequest orderStatusRequest = new OrderStatusRequest("DELIVERED");

    when(orderStatusRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderStatusService.updateOrderStatus(1L, orderStatusRequest));
  }

  @Test
  public void shouldNotThrowExceptionWhenUpdatingOrderStatusWithSameName() {
    OrderStatusRequest orderStatusRequest = new OrderStatusRequest("ACCEPTED");
    OrderStatusEntity orderStatusEntity = new OrderStatusEntity(1L, "ACCEPTED");

    when(orderStatusRepository.findById(1L)).thenReturn(Optional.of(orderStatusEntity));
    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest.getName()))
        .thenReturn(true);

    assertDoesNotThrow(() -> orderStatusService.updateOrderStatus(1L, orderStatusRequest));
  }

  @Test
  public void shouldDeleteOrderStatusById() {
    when(orderStatusRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> orderStatusService.deleteOrderStatus(1L));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenOrderStatusNotFoundOnDelete() {
    when(orderStatusRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> orderStatusService.deleteOrderStatus(1L));
  }

  @Test
  public void shouldThrowDeletionNotAllowedExceptionWhenDeletingOrderStatusWithDependencies() {
    when(orderStatusRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(orderStatusRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> orderStatusService.deleteOrderStatus(1L));
  }

  @Test
  public void shouldThrowExceptionWhenDataIntegrityViolationIsNotCausedByConstraintViolation() {
    when(orderStatusRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(orderStatusRepository)
        .deleteById(1L);

    assertThrows(
        DataIntegrityViolationException.class, () -> orderStatusService.deleteOrderStatus(1L));
  }
}
