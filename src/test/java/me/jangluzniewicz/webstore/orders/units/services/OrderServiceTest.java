package me.jangluzniewicz.webstore.orders.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.OrderStatusNotAllowedException;
import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.mappers.OrderMapper;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.orders.repositories.OrderRepository;
import me.jangluzniewicz.webstore.orders.services.OrderService;
import me.jangluzniewicz.webstore.orderstatuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.orderstatuses.models.OrderStatus;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.utils.testdata.order_statuses.OrderStatusEntityTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.order_statuses.OrderStatusTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.orders.*;
import me.jangluzniewicz.webstore.utils.testdata.products.ProductTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.users.UserTestDataBuilder;
import me.jangluzniewicz.webstore.utils.units.config.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

class OrderServiceTest extends UnitTest {
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
  private ChangeOrderStatusRequest changeOrderStatusRequest;
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
            .items(List.of(OrderItemRequestTestDataBuilder.builder().quantity(3).build()))
            .build()
            .buildOrderRequest();
    orderFilterRequest =
        OrderFilterRequestTestDataBuilder.builder().build().buildOrderFilterRequest();
    changeOrderStatusRequest =
        ChangeOrderStatusRequestTestDataBuilder.builder().build().buildChangeOrderStatusRequest();
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
  void createNewOrder_whenUserIsGuest_thenReturnIdResponse() {
    OrderRequest guestOrderRequest =
        OrderRequestTestDataBuilder.builder().customerId(null).build().buildOrderRequest();

    when(userService.getUserByEmail("GUEST")).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
    when(orderRepository.save(any())).thenReturn(orderEntity);

    assertEquals(orderEntity.getId(), orderService.createNewOrder(guestOrderRequest).getId());
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
  @SuppressWarnings("unchecked")
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
    when(orderStatusService.getOrderStatusById(changeOrderStatusRequest.getOrderStatusId()))
        .thenReturn(Optional.of(orderStatus));
    when(orderRepository.save(any())).thenReturn(orderEntity);

    assertDoesNotThrow(
        () -> orderService.changeOrderStatus(orderEntity.getId(), changeOrderStatusRequest));
  }

  @Test
  void changeOrderStatus_whenOrderDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.changeOrderStatus(orderEntity.getId(), changeOrderStatusRequest));
  }

  @Test
  void changeOrderStatus_whenOrderStatusDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(orderStatusService.getOrderStatusById(changeOrderStatusRequest.getOrderStatusId()))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.changeOrderStatus(orderEntity.getId(), changeOrderStatusRequest));
  }

  @Test
  void changeOrderStatus_whenCurrentStatusIsCancelled_thenThrowOrderStatusNotAllowedException() {
    OrderEntity cancelledOrderEntity =
        OrderEntityTestDataBuilder.builder()
            .orderStatusEntityBuilder(OrderStatusEntityTestDataBuilder.builder().id(3L).build())
            .build()
            .buildOrderEntity();
    when(orderRepository.findById(cancelledOrderEntity.getId()))
        .thenReturn(Optional.of(cancelledOrderEntity));
    Order cancelledOrder =
        OrderTestDataBuilder.builder()
            .orderStatusBuilder(OrderStatusTestDataBuilder.builder().id(3L).build())
            .build()
            .buildOrder();
    when(orderMapper.fromEntity(cancelledOrderEntity)).thenReturn(cancelledOrder);

    assertThrows(
        OrderStatusNotAllowedException.class,
        () ->
            orderService.changeOrderStatus(cancelledOrderEntity.getId(), changeOrderStatusRequest));
  }

  @Test
  void changeOrderStatus_whenCurrentStatusIsCompleted_thenThrowOrderStatusNotAllowedException() {
    OrderEntity completedEntity =
        OrderEntityTestDataBuilder.builder()
            .orderStatusEntityBuilder(OrderStatusEntityTestDataBuilder.builder().id(4L).build())
            .build()
            .buildOrderEntity();
    when(orderRepository.findById(completedEntity.getId()))
        .thenReturn(Optional.of(completedEntity));
    Order completedOrder =
        OrderTestDataBuilder.builder()
            .orderStatusBuilder(OrderStatusTestDataBuilder.builder().id(4L).build())
            .build()
            .buildOrder();
    when(orderMapper.fromEntity(completedEntity)).thenReturn(completedOrder);

    assertThrows(
        OrderStatusNotAllowedException.class,
        () -> orderService.changeOrderStatus(completedEntity.getId(), changeOrderStatusRequest));
  }

  @Test
  void addRatingToOrder_whenOrderExistsAndRatingIsNull_thenAddRating() {
    OrderEntity completedOrderEntity =
        OrderEntityTestDataBuilder.builder()
            .orderStatusEntityBuilder(OrderStatusEntityTestDataBuilder.builder().id(4L).build())
            .build()
            .buildOrderEntity();
    Order completedOrder =
        OrderTestDataBuilder.builder()
            .orderStatusBuilder(OrderStatusTestDataBuilder.builder().id(4L).build())
            .build()
            .buildOrder();
    when(orderRepository.findById(completedOrderEntity.getId()))
        .thenReturn(Optional.of(completedOrderEntity));
    when(orderMapper.fromEntity(completedOrderEntity)).thenReturn(completedOrder);
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
  void updateOrder_WhenOrderExistsAndRequestIsValid_thenUpdateOrder() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(userService.getUserById(orderRequest2.getCustomerId())).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
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
  void updateOrder_WhenProductDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity)).thenReturn(order);
    when(userService.getUserById(orderRequest2.getCustomerId())).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.updateOrder(orderEntity.getId(), orderRequest2));
  }

  @Test
  void getOrderOwnerId_whenOrderExists_thenReturnOwnerId() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));

    assertEquals(user.getId(), orderService.getOrderOwnerId(orderEntity.getId()));
  }

  @Test
  void getOrderOwnerId_whenOrderDoesNotExist_thenThrowNotFoundException() {
    when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> orderService.getOrderOwnerId(orderEntity.getId()));
  }
}
