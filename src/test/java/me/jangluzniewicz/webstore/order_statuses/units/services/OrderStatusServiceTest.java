package me.jangluzniewicz.webstore.order_statuses.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest {
  @Mock private OrderStatusRepository orderStatusRepository;
  @Mock private OrderStatusMapper orderStatusMapper;
  @InjectMocks private OrderStatusService orderStatusService;

  @Test
  public void createNewOrderStatus_whenOrderStatusDoesNotExist_thenReturnOrderStatusId() {
    when(orderStatusRepository.existsByNameIgnoreCase("ACCEPTED")).thenReturn(false);
    when(orderStatusRepository.save(any())).thenReturn(new OrderStatusEntity(1L, "ACCEPTED"));

    assertEquals(1L, orderStatusService.createNewOrderStatus(new OrderStatusRequest("ACCEPTED")));
  }

  @Test
  public void createNewOrderStatus_whenOrderStatusAlreadyExists_thenThrowNotUniqueException() {
    when(orderStatusRepository.existsByNameIgnoreCase("ACCEPTED")).thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () -> orderStatusService.createNewOrderStatus(new OrderStatusRequest("ACCEPTED")));
  }

  @Test
  public void getOrderStatusById_whenOrderStatusExists_thenReturnOrderStatus() {
    when(orderStatusRepository.findById(1L))
        .thenReturn(Optional.of(new OrderStatusEntity(1L, "ACCEPTED")));
    when(orderStatusMapper.fromEntity(any())).thenReturn(new OrderStatus(1L, "ACCEPTED"));

    assertTrue(orderStatusService.getOrderStatusById(1L).isPresent());
  }

  @Test
  public void getOrderStatusById_whenOrderStatusDoesNotExist_thenReturnEmpty() {
    when(orderStatusRepository.findById(1L)).thenReturn(Optional.empty());

    assertTrue(orderStatusService.getOrderStatusById(1L).isEmpty());
  }

  @Test
  public void getAllOrderStatuses_whenOrderStatusesExist_thenReturnPagedResponse() {
    Page<OrderStatusEntity> page = new PageImpl<>(List.of(new OrderStatusEntity(1L, "ACCEPTED")));

    when(orderStatusRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(orderStatusMapper.fromEntity(any())).thenReturn(new OrderStatus(1L, "ACCEPTED"));

    assertEquals(1, orderStatusService.getAllOrderStatuses(0, 10).getTotalPages());
  }

  @Test
  public void updateOrderStatus_whenOrderStatusExistsAndNewNameIsUnique_thenReturnOrderStatusId() {
    when(orderStatusRepository.findById(1L))
        .thenReturn(Optional.of(new OrderStatusEntity(1L, "ACCEPTED")));
    when(orderStatusRepository.existsByNameIgnoreCase("DELIVERED")).thenReturn(false);

    assertEquals(1L, orderStatusService.updateOrderStatus(1L, new OrderStatusRequest("DELIVERED")));
  }

  @Test
  public void
      updateOrderStatus_whenOrderStatusExistsAndNewNameAlreadyExists_thenThrowNotUniqueException() {
    when(orderStatusRepository.findById(1L))
        .thenReturn(Optional.of(new OrderStatusEntity(1L, "ACCEPTED")));
    when(orderStatusRepository.existsByNameIgnoreCase("DELIVERED")).thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () -> orderStatusService.updateOrderStatus(1L, new OrderStatusRequest("DELIVERED")));
  }

  @Test
  public void updateOrderStatus_whenOrderStatusDoesNotExist_thenThrowNotFoundException() {
    when(orderStatusRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderStatusService.updateOrderStatus(1L, new OrderStatusRequest("DELIVERED")));
  }

  @Test
  public void updateOrderStatus_whenOrderStatusExistsAndNewNameIsSame_thenDoNotThrowException() {
    when(orderStatusRepository.findById(1L))
        .thenReturn(Optional.of(new OrderStatusEntity(1L, "ACCEPTED")));
    when(orderStatusRepository.existsByNameIgnoreCase("ACCEPTED")).thenReturn(false);

    assertDoesNotThrow(
        () -> orderStatusService.updateOrderStatus(1L, new OrderStatusRequest("ACCEPTED")));
  }

  @Test
  public void deleteOrderStatus_whenOrderStatusExists_thenDeleteSuccessfully() {
    when(orderStatusRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> orderStatusService.deleteOrderStatus(1L));
  }

  @Test
  public void deleteOrderStatus_whenOrderStatusDoesNotExist_thenThrowNotFoundException() {
    when(orderStatusRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> orderStatusService.deleteOrderStatus(1L));
  }

  @Test
  public void
      deleteOrderStatus_whenOrderStatusHasDependencies_thenThrowDeletionNotAllowedException() {
    when(orderStatusRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(orderStatusRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> orderStatusService.deleteOrderStatus(1L));
  }

  @Test
  public void
      deleteOrderStatus_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(orderStatusRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(orderStatusRepository)
        .deleteById(1L);

    assertThrows(
        DataIntegrityViolationException.class, () -> orderStatusService.deleteOrderStatus(1L));
  }
}
