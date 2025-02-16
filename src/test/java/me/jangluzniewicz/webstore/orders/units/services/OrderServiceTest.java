package me.jangluzniewicz.webstore.orders.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.testdata.order_statuses.OrderStatusTestDataBuilder;
import me.jangluzniewicz.webstore.common.testdata.orders.*;
import me.jangluzniewicz.webstore.common.testdata.products.ProductTestDataBuilder;
import me.jangluzniewicz.webstore.common.testdata.users.UserTestDataBuilder;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.mappers.OrderMapper;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.orders.repositories.OrderRepository;
import me.jangluzniewicz.webstore.orders.services.OrderService;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import me.jangluzniewicz.webstore.users.models.User;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
  @Mock private OrderRepository orderRepository;
  @Mock private IOrderStatus orderStatusService;
  @Mock private IUser userService;
  @Mock private IProduct productService;
  @Mock private OrderMapper orderMapper;
  @InjectMocks private OrderService orderService;

  private User user;
  private OrderStatus orderStatus;
  private Product product;
  private OrderEntity orderEntity;
  private Order order;
  private OrderRequest orderRequest1;
  private OrderRequest orderRequest2;
  private OrderStatusRequest orderStatusRequest;
  private OrderFilterRequest orderFilterRequest;
  private RatingRequest ratingRequest;

  @BeforeEach
  void setUp() {
    user = UserTestDataBuilder.builder().build().buildUser();
    orderStatus = OrderStatusTestDataBuilder.builder().build().buildOrderStatus();
    product = ProductTestDataBuilder.builder().build().buildProduct();
    orderEntity = OrderEntityTestDataBuilder.builder().build().buildOrderEntity();
    order = OrderTestDataBuilder.builder().build().buildOrder();
    orderRequest1 = OrderRequestTestDataBuilder.builder().build().buildOrderRequest();
    orderRequest2 =
        OrderRequestTestDataBuilder.builder()
            .ratingBuilder(RatingRequestTestDataBuilder.builder().build())
            .items(List.of(OrderItemRequestTestDataBuilder.builder().quantity(3).build()))
            .build()
            .buildOrderRequest();
    orderFilterRequest =
        OrderFilterRequestTestDataBuilder.builder().build().buildOrderFilterRequest();
    orderStatusRequest =
        OrderStatusRequestTestDataBuilder.builder().build().buildChangeOrderStatusRequest();
    ratingRequest = RatingRequestTestDataBuilder.builder().build().buildRatingRequest();
  }

  @Test
  void createNewOrder_whenUserExistsAndProductExists_thenReturnIdResponse() {
    when(userService.getUserById(orderRequest1.getCustomerId())).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
    when(orderRepository.save(any())).thenReturn(orderEntity);

    assertEquals(orderEntity.getId(), orderService.createNewOrder(orderRequest1).getId());
  }

  @Test
  void createNewOrder_whenUserDoesNotExist_thenThrowNotFoundException() {
    when(userService.getUserById(orderRequest1.getCustomerId())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> orderService.createNewOrder(orderRequest1));
  }

  @Test
  void createNewOrder_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(userService.getUserById(orderRequest1.getCustomerId())).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> orderService.createNewOrder(orderRequest1));
  }

  @Test
  void getOrderById_whenOrderExists_thenReturnOrder() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);

    assertTrue(orderService.getOrderById(orderEntity.getId()).isPresent());
  }

  @Test
  void getOrderById_whenOrderDoesNotExist_thenReturnEmpty() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.empty());

    assertTrue(orderService.getOrderById(orderEntity.getId()).isEmpty());
  }

  @Test
  void getOrdersByCustomerId_whenOrdersExist_thenReturnPagedResponse() {
    Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity));
    when(orderRepository.findAllByCustomerIdOrderByOrderDateAscIdAsc(
            anyLong(), any(Pageable.class)))
        .thenReturn(page);
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);

    assertEquals(1, orderService.getOrdersByCustomerId(user.getId(), 0, 10).getTotalPages());
  }

  @Test
  void getAllOrders_whenOrdersExist_thenReturnPagedResponse() {
    Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity));
    when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);

    assertEquals(1, orderService.getAllOrders(0, 10).getTotalPages());
  }

  @Test
  void getFilteredOrders_whenOrdersExist_thenReturnPagedResponse() {
    Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity));
    when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);

    assertEquals(1, orderService.getFilteredOrders(orderFilterRequest, 0, 10).getTotalPages());
  }

  @Test
  void changeOrderStatus_whenOrderExistsAndOrderStatusExists_thenUpdateOrderStatus() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(orderStatusService.getOrderStatusById(orderStatusRequest.getOrderStatusId()))
        .thenReturn(Optional.of(orderStatus));
    when(orderRepository.save(any())).thenReturn(orderEntity);

    assertDoesNotThrow(
        () -> orderService.changeOrderStatus(orderEntity.getId(), orderStatusRequest));
  }

  @Test
  void changeOrderStatus_whenOrderDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.changeOrderStatus(orderEntity.getId(), orderStatusRequest));
  }

  @Test
  void changeOrderStatus_whenOrderStatusDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(orderStatusService.getOrderStatusById(orderStatusRequest.getOrderStatusId()))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.changeOrderStatus(orderEntity.getId(), orderStatusRequest));
  }

  @Test
  void addRatingToOrder_whenOrderExistsAndRatingIsNull_thenAddRating() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    OrderEntity orderWithRating =
        OrderEntityTestDataBuilder.builder()
            .ratingEntityBuilder(RatingEntityTestDataBuilder.builder().build())
            .build()
            .buildOrderEntity();
    when(orderRepository.save(any())).thenReturn(orderWithRating);

    assertDoesNotThrow(() -> orderService.addRatingToOrder(orderEntity.getId(), ratingRequest));
  }

  @Test
  void addRatingToOrder_whenOrderDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.addRatingToOrder(orderEntity.getId(), ratingRequest));
  }

  @Test
  void addRatingToOrder_whenRatingExists_thenThrowConflictException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity))
        .thenReturn(
            OrderTestDataBuilder.builder()
                .ratingBuilder(RatingTestDataBuilder.builder().build())
                .build()
                .buildOrder());

    assertThrows(
        ConflictException.class,
        () -> orderService.addRatingToOrder(orderEntity.getId(), ratingRequest));
  }

  @Test
  void deleteOrder_whenOrderExists_thenDeleteSuccessfully() {
    when(orderRepository.existsById(orderEntity.getId())).thenReturn(true);

    assertDoesNotThrow(() -> orderService.deleteOrder(orderEntity.getId()));
  }

  @Test
  void deleteOrder_whenOrderDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.existsById(orderEntity.getId())).thenReturn(false);

    assertThrows(NotFoundException.class, () -> orderService.deleteOrder(orderEntity.getId()));
  }

  @Test
  void deleteOrder_whenOrderHasDependencies_thenThrowDeletionNotAllowedException() {
    when(orderRepository.existsById(orderEntity.getId())).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(orderRepository)
        .deleteById(orderEntity.getId());

    assertThrows(
        DeletionNotAllowedException.class, () -> orderService.deleteOrder(orderEntity.getId()));
  }

  @Test
  void deleteOrder_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(orderRepository.existsById(orderEntity.getId())).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(orderRepository)
        .deleteById(orderEntity.getId());

    assertThrows(
        DataIntegrityViolationException.class, () -> orderService.deleteOrder(orderEntity.getId()));
  }

  @Test
  void updateOrder_WhenOrderExistsAndRequestIsValid_thenUpdateOrder() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(userService.getUserById(orderRequest2.getCustomerId())).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
    when(orderStatusService.getOrderStatusById(orderRequest2.getStatusId()))
        .thenReturn(Optional.of(orderStatus));
    OrderEntity updatedOrderEntity =
        OrderEntityTestDataBuilder.builder()
            .items(List.of(OrderItemEntityTestDataBuilder.builder().quantity(3).build()))
            .build()
            .buildOrderEntity();
    when(orderRepository.save(any())).thenReturn(updatedOrderEntity);

    assertDoesNotThrow(() -> orderService.updateOrder(orderEntity.getId(), orderRequest2));
  }

  @Test
  void updateOrder_WhenOrderExistsAndMinimumRequestValuesAreProvided_thenUpdateOrder() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(userService.getUserById(orderRequest2.getCustomerId())).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
    when(orderStatusService.getOrderStatusById(orderRequest2.getStatusId()))
        .thenReturn(Optional.of(orderStatus));
    when(orderRepository.save(any())).thenReturn(orderEntity);

    assertDoesNotThrow(() -> orderService.updateOrder(orderEntity.getId(), orderRequest1));
  }

  @Test
  void updateOrder_WhenOrderDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.updateOrder(orderEntity.getId(), orderRequest2));
  }

  @Test
  void updateOrder_WhenUserDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(userService.getUserById(orderRequest2.getCustomerId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.updateOrder(orderEntity.getId(), orderRequest2));
  }

  @Test
  void updateOrder_WhenOrderStatusDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(userService.getUserById(orderRequest2.getCustomerId())).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
    when(orderStatusService.getOrderStatusById(orderRequest2.getStatusId()))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.updateOrder(orderEntity.getId(), orderRequest2));
  }

  @Test
  void updateOrder_WhenProductDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(userService.getUserById(orderRequest2.getCustomerId())).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.updateOrder(orderEntity.getId(), orderRequest2));
  }
}
