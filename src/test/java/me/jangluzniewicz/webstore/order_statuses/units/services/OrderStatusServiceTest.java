package me.jangluzniewicz.webstore.order_statuses.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.units.BaseServiceUnitTest;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.order_statuses.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.mappers.OrderStatusMapper;
import me.jangluzniewicz.webstore.order_statuses.repositories.OrderStatusRepository;
import me.jangluzniewicz.webstore.order_statuses.services.OrderStatusService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest extends BaseServiceUnitTest {
  @Mock private OrderStatusRepository orderStatusRepository;
  @Mock private OrderStatusMapper orderStatusMapper;
  @InjectMocks private OrderStatusService orderStatusService;

  private OrderStatusRequest orderStatusRequest1;
  private OrderStatusRequest orderStatusRequest2;

  @BeforeEach
  void setUp() {
    orderStatusRequest1 = new OrderStatusRequest("PENDING");
    orderStatusRequest2 = new OrderStatusRequest("DELIVERED");
  }

  @Test
  void createNewOrderStatus_whenOrderStatusDoesNotExist_thenReturnOrderStatusId() {
    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest1.getName()))
        .thenReturn(false);
    when(orderStatusRepository.save(any())).thenReturn(orderStatusEntity);

    assertEquals(1L, orderStatusService.createNewOrderStatus(orderStatusRequest1));
  }

  @Test
  void createNewOrderStatus_whenOrderStatusAlreadyExists_thenThrowNotUniqueException() {
    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest1.getName()))
        .thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () -> orderStatusService.createNewOrderStatus(orderStatusRequest1));
  }

  @Test
  void getOrderStatusById_whenOrderStatusExists_thenReturnOrderStatus() {
    when(orderStatusRepository.findById(1L)).thenReturn(Optional.of(orderStatusEntity));
    when(orderStatusMapper.fromEntity(any())).thenReturn(orderStatus);

    assertTrue(orderStatusService.getOrderStatusById(1L).isPresent());
  }

  @Test
  void getOrderStatusById_whenOrderStatusDoesNotExist_thenReturnEmpty() {
    when(orderStatusRepository.findById(1L)).thenReturn(Optional.empty());

    assertTrue(orderStatusService.getOrderStatusById(1L).isEmpty());
  }

  @Test
  void getAllOrderStatuses_whenOrderStatusesExist_thenReturnPagedResponse() {
    when(orderStatusRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(orderStatusEntity)));
    when(orderStatusMapper.fromEntity(any())).thenReturn(orderStatus);

    assertEquals(1, orderStatusService.getAllOrderStatuses(0, 10).getTotalPages());
  }

  @Test
  void updateOrderStatus_whenOrderStatusExistsAndNewNameIsUnique_thenReturnOrderStatusId() {
    when(orderStatusRepository.findById(1L)).thenReturn(Optional.of(orderStatusEntity));
    when(orderStatusMapper.fromEntity(any())).thenReturn(orderStatus);
    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest2.getName()))
        .thenReturn(false);
    OrderStatusEntity updatedEntity =
        OrderStatusEntity.builder()
            .id(orderStatusEntity.getId())
            .name(orderStatusRequest2.getName())
            .build();
    when(orderStatusRepository.save(any())).thenReturn(updatedEntity);

    assertEquals(1L, orderStatusService.updateOrderStatus(1L, orderStatusRequest2));
  }

  @Test
  void
      updateOrderStatus_whenOrderStatusExistsAndNewNameAlreadyExists_thenThrowNotUniqueException() {
    when(orderStatusRepository.findById(1L)).thenReturn(Optional.of(orderStatusEntity));
    when(orderStatusMapper.fromEntity(any())).thenReturn(orderStatus);
    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest2.getName()))
        .thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () -> orderStatusService.updateOrderStatus(1L, orderStatusRequest2));
  }

  @Test
  void updateOrderStatus_whenOrderStatusDoesNotExist_thenThrowNotFoundException() {
    when(orderStatusRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderStatusService.updateOrderStatus(1L, orderStatusRequest2));
  }

  @Test
  void updateOrderStatus_whenOrderStatusExistsAndNewNameIsSame_thenDoNotThrowException() {
    when(orderStatusRepository.findById(1L)).thenReturn(Optional.of(orderStatusEntity));
    when(orderStatusMapper.fromEntity(any())).thenReturn(orderStatus);
    when(orderStatusRepository.existsByNameIgnoreCase(orderStatusRequest1.getName()))
        .thenReturn(true);
    when(orderStatusRepository.save(any())).thenReturn(orderStatusEntity);

    assertDoesNotThrow(() -> orderStatusService.updateOrderStatus(1L, orderStatusRequest1));
  }

  @Test
  void deleteOrderStatus_whenOrderStatusExists_thenDeleteSuccessfully() {
    when(orderStatusRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> orderStatusService.deleteOrderStatus(1L));
  }

  @Test
  void deleteOrderStatus_whenOrderStatusDoesNotExist_thenThrowNotFoundException() {
    when(orderStatusRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> orderStatusService.deleteOrderStatus(1L));
  }

  @Test
  void deleteOrderStatus_whenOrderStatusHasDependencies_thenThrowDeletionNotAllowedException() {
    when(orderStatusRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(orderStatusRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> orderStatusService.deleteOrderStatus(1L));
  }

  @Test
  void deleteOrderStatus_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(orderStatusRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(orderStatusRepository)
        .deleteById(1L);

    assertThrows(
        DataIntegrityViolationException.class, () -> orderStatusService.deleteOrderStatus(1L));
  }
}
