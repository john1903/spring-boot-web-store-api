package me.jangluzniewicz.webstore.orders.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.order_statuses.mappers.OrderStatusMapper;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderItemRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.entities.OrderItemEntity;
import me.jangluzniewicz.webstore.orders.entities.RatingEntity;
import me.jangluzniewicz.webstore.orders.mappers.OrderItemMapper;
import me.jangluzniewicz.webstore.orders.mappers.OrderMapper;
import me.jangluzniewicz.webstore.orders.mappers.RatingMapper;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.orders.models.OrderItem;
import me.jangluzniewicz.webstore.orders.models.Rating;
import me.jangluzniewicz.webstore.orders.repositories.OrderRepository;
import me.jangluzniewicz.webstore.orders.services.OrderService;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
  @Mock private OrderRepository orderRepository;
  @Mock private IOrderStatus orderStatusService;
  @Mock private IUser userService;
  @Mock private IProduct productService;
  @Mock private OrderMapper orderMapper;
  @Mock private UserMapper userMapper;
  @Mock private OrderStatusMapper orderStatusMapper;
  @Mock private OrderItemMapper orderItemMapper;
  @Mock private RatingMapper ratingMapper;
  @InjectMocks private OrderService orderService;
  private ProductEntity productEntity;
  private Product product;
  private UserEntity userEntity;
  private User user;
  private OrderStatusEntity orderStatusEntity;
  private OrderStatus orderStatus;

  @BeforeEach
  void setUp() {
    productEntity =
        ProductEntity.builder()
            .id(1L)
            .name("Bicycle")
            .description("Mountain bike")
            .price(BigDecimal.valueOf(1000.0))
            .weight(BigDecimal.valueOf(10.0))
            .category(new CategoryEntity(1L, "Bicycles"))
            .build();
    product =
        Product.builder()
            .id(1L)
            .name("Bicycle")
            .description("Mountain bike")
            .price(BigDecimal.valueOf(1000.0))
            .weight(BigDecimal.valueOf(10.0))
            .category(new Category(1L, "Bicycles"))
            .build();
    userEntity =
        UserEntity.builder()
            .id(1L)
            .email("admin@admin.com")
            .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
            .role(new RoleEntity(1L, "ADMIN"))
            .phoneNumber("+48123456789")
            .build();
    user =
        User.builder()
            .email("admin@admin.com")
            .password("$2a$12$YMHq03Ob7Jq9LWg.rnQPv.fy/21taNY4dmenw5HOkZJ7YI.4ryMOC")
            .role(new Role(1L, "ADMIN"))
            .phoneNumber("+48123456789")
            .build();
    orderStatusEntity = new OrderStatusEntity(1L, "NEW");
    orderStatus = new OrderStatus(1L, "NEW");
  }

  @Test
  public void shouldCreateNewOrderAndReturnOrderId() {
    OrderRequest orderRequest =
        new OrderRequest(
            null,
            null,
            1L,
            null,
            null,
            List.of(
                new OrderItemRequest(null, 1L, 1, BigDecimal.valueOf(1000.0), BigDecimal.ZERO)));
    OrderEntity savedEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();

    when(userService.getUserById(1L)).thenReturn(Optional.of(user));
    when(productService.getProductById(1L)).thenReturn(Optional.of(product));
    when(orderMapper.toEntity(any()))
        .thenReturn(
            OrderEntity.builder()
                .orderDate(LocalDateTime.now())
                .statusChangeDate(LocalDateTime.now())
                .customer(userEntity)
                .statusChangeDate(LocalDateTime.now())
                .status(orderStatusEntity)
                .items(
                    List.of(
                        OrderItemEntity.builder()
                            .product(productEntity)
                            .quantity(1)
                            .price(BigDecimal.valueOf(1000.0))
                            .discount(BigDecimal.ZERO)
                            .build()))
                .build());
    when(orderRepository.save(any())).thenReturn(savedEntity);

    Long orderId = orderService.createNewOrder(orderRequest);

    assertEquals(1L, orderId);
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenUserNotFoundOnCreate() {
    OrderRequest orderRequest =
        new OrderRequest(
            null,
            null,
            1L,
            null,
            null,
            List.of(
                new OrderItemRequest(null, 1L, 1, BigDecimal.valueOf(1000.0), BigDecimal.ZERO)));

    when(userService.getUserById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> orderService.createNewOrder(orderRequest));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenProductNotFoundOnCreate() {
    OrderRequest orderRequest =
        new OrderRequest(
            null,
            null,
            1L,
            null,
            null,
            List.of(
                new OrderItemRequest(null, 1L, 1, BigDecimal.valueOf(1000.0), BigDecimal.ZERO)));

    when(userService.getUserById(1L)).thenReturn(Optional.of(user));
    when(productService.getProductById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> orderService.createNewOrder(orderRequest));
  }

  @Test
  public void shouldReturnOrderWhenGettingOrderById() {
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
    when(orderMapper.fromEntity(orderEntity))
        .thenReturn(
            Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .statusChangeDate(LocalDateTime.now())
                .customer(user)
                .statusChangeDate(LocalDateTime.now())
                .status(orderStatus)
                .items(
                    List.of(
                        OrderItem.builder()
                            .id(1L)
                            .product(product)
                            .quantity(1)
                            .price(BigDecimal.valueOf(1000.0))
                            .discount(BigDecimal.ZERO)
                            .build()))
                .build());

    Optional<Order> order = orderService.getOrderById(1L);

    assertTrue(order.isPresent());
    assertEquals(1L, order.get().getId());
    assertEquals(1, order.get().getItems().size());
    assertEquals(1L, order.get().getItems().getFirst().getId());
  }

  @Test
  public void shouldReturnEmptyWhenOrderNotFoundById() {
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<Order> order = orderService.getOrderById(1L);

    assertTrue(order.isEmpty());
  }

  @Test
  public void shouldReturnPagedResponseWhenGettingOrdersByUserId() {
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();
    Pageable pageable = PageRequest.of(0, 10);
    Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity), pageable, 1);

    when(orderRepository.findAllByCustomerIdOrderByOrderDateAscIdAsc(1L, pageable))
        .thenReturn(page);
    when(orderMapper.fromEntity(orderEntity))
        .thenReturn(
            Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .statusChangeDate(LocalDateTime.now())
                .customer(user)
                .statusChangeDate(LocalDateTime.now())
                .status(orderStatus)
                .items(
                    List.of(
                        OrderItem.builder()
                            .id(1L)
                            .product(product)
                            .quantity(1)
                            .price(BigDecimal.valueOf(1000.0))
                            .discount(BigDecimal.ZERO)
                            .build()))
                .build());

    PagedResponse<Order> orders = orderService.getOrdersByCustomerId(1L, 0, 10);

    assertEquals(1, orders.getTotalPages());
    assertEquals(1, orders.getContent().size());
    assertEquals(1L, orders.getContent().getFirst().getId());
    assertEquals(1, orders.getContent().getFirst().getItems().size());
    assertEquals(1L, orders.getContent().getFirst().getItems().getFirst().getId());
  }

  @Test
  public void shouldReturnPagedResponseWhenGettingAllOrders() {
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();
    Pageable pageable = PageRequest.of(0, 10);
    Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity), pageable, 1);

    when(orderRepository.findAll(pageable)).thenReturn(page);
    when(orderMapper.fromEntity(orderEntity))
        .thenReturn(
            Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .statusChangeDate(LocalDateTime.now())
                .customer(user)
                .statusChangeDate(LocalDateTime.now())
                .status(orderStatus)
                .items(
                    List.of(
                        OrderItem.builder()
                            .id(1L)
                            .product(product)
                            .quantity(1)
                            .price(BigDecimal.valueOf(1000.0))
                            .discount(BigDecimal.ZERO)
                            .build()))
                .build());

    PagedResponse<Order> orders = orderService.getAllOrders(0, 10);

    assertEquals(1, orders.getTotalPages());
    assertEquals(1, orders.getContent().size());
    assertEquals(1L, orders.getContent().getFirst().getId());
    assertEquals(1, orders.getContent().getFirst().getItems().size());
    assertEquals(1L, orders.getContent().getFirst().getItems().getFirst().getId());
  }

  @Test
  public void shouldReturnPagedResponseWhenGettingFilteredOrdersWithoutAnyFilters() {
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();
    Pageable pageable = PageRequest.of(0, 10);
    Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity), pageable, 1);

    when(orderRepository.findAll(pageable)).thenReturn(page);
    when(orderMapper.fromEntity(orderEntity))
        .thenReturn(
            Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .statusChangeDate(LocalDateTime.now())
                .customer(user)
                .statusChangeDate(LocalDateTime.now())
                .status(orderStatus)
                .items(
                    List.of(
                        OrderItem.builder()
                            .id(1L)
                            .product(product)
                            .quantity(1)
                            .price(BigDecimal.valueOf(1000.0))
                            .discount(BigDecimal.ZERO)
                            .build()))
                .build());

    PagedResponse<Order> orders = orderService.getFilteredOrders(null, null, null, 0, 10);

    assertEquals(1, orders.getTotalPages());
    assertEquals(1, orders.getContent().size());
    assertEquals(1L, orders.getContent().getFirst().getId());
    assertEquals(1, orders.getContent().getFirst().getItems().size());
    assertEquals(1L, orders.getContent().getFirst().getItems().getFirst().getId());
  }

  @Test
  public void shouldReturnPagedResponseWhenGettingFilteredOrdersWithCustomerIdFilter() {
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.of(2025, 2, 7, 16, 15))
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();
    Pageable pageable = PageRequest.of(0, 10);
    Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity), pageable, 1);

    when(orderRepository.findAllByStatusIdOrderByOrderDateAscIdAsc(1L, pageable)).thenReturn(page);
    when(orderMapper.fromEntity(orderEntity))
        .thenReturn(
            Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .statusChangeDate(LocalDateTime.now())
                .customer(user)
                .statusChangeDate(LocalDateTime.now())
                .status(orderStatus)
                .items(
                    List.of(
                        OrderItem.builder()
                            .id(1L)
                            .product(product)
                            .quantity(1)
                            .price(BigDecimal.valueOf(1000.0))
                            .discount(BigDecimal.ZERO)
                            .build()))
                .build());

    PagedResponse<Order> orders = orderService.getFilteredOrders(1L, null, null, 0, 10);

    assertEquals(1, orders.getTotalPages());
    assertEquals(1, orders.getContent().size());
    assertEquals(1L, orders.getContent().getFirst().getStatus().getId());
    assertEquals(1L, orders.getContent().getFirst().getId());
    assertEquals(1, orders.getContent().getFirst().getItems().size());
    assertEquals(1L, orders.getContent().getFirst().getItems().getFirst().getId());
  }

  @Test
  public void shouldReturnPagedResponseWhenGettingFilteredOrdersWithDateRangeFilter() {
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();
    Pageable pageable = PageRequest.of(0, 10);
    Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity), pageable, 1);

    when(orderRepository.findAllByOrderDateBetweenOrderByOrderDateAscIdAsc(
            LocalDateTime.of(2025, 2, 7, 16, 0), LocalDateTime.of(2025, 2, 7, 16, 30), pageable))
        .thenReturn(page);
    when(orderMapper.fromEntity(orderEntity))
        .thenReturn(
            Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .statusChangeDate(LocalDateTime.now())
                .customer(user)
                .statusChangeDate(LocalDateTime.now())
                .status(orderStatus)
                .items(
                    List.of(
                        OrderItem.builder()
                            .id(1L)
                            .product(product)
                            .quantity(1)
                            .price(BigDecimal.valueOf(1000.0))
                            .discount(BigDecimal.ZERO)
                            .build()))
                .build());

    PagedResponse<Order> orders =
        orderService.getFilteredOrders(
            null, LocalDateTime.of(2025, 2, 7, 16, 0), LocalDateTime.of(2025, 2, 7, 16, 30), 0, 10);

    assertEquals(1, orders.getTotalPages());
    assertEquals(1, orders.getContent().size());
    assertEquals(1L, orders.getContent().getFirst().getId());
    assertEquals(1, orders.getContent().getFirst().getItems().size());
    assertEquals(1L, orders.getContent().getFirst().getItems().getFirst().getId());
  }

  @Test
  public void shouldReturnPagedResponseWhenGettingFilteredOrdersWithCustomerIdAndDateRangeFilter() {
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();
    Pageable pageable = PageRequest.of(0, 10);
    Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity), pageable, 1);

    when(orderRepository.findAllByStatusIdAndOrderDateBetweenOrderByOrderDateAscIdAsc(
            1L,
            LocalDateTime.of(2025, 2, 7, 16, 0),
            LocalDateTime.of(2025, 2, 7, 16, 30),
            pageable))
        .thenReturn(page);
    when(orderMapper.fromEntity(orderEntity))
        .thenReturn(
            Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .statusChangeDate(LocalDateTime.now())
                .customer(user)
                .statusChangeDate(LocalDateTime.now())
                .status(orderStatus)
                .items(
                    List.of(
                        OrderItem.builder()
                            .id(1L)
                            .product(product)
                            .quantity(1)
                            .price(BigDecimal.valueOf(1000.0))
                            .discount(BigDecimal.ZERO)
                            .build()))
                .build());

    PagedResponse<Order> orders =
        orderService.getFilteredOrders(
            1L, LocalDateTime.of(2025, 2, 7, 16, 0), LocalDateTime.of(2025, 2, 7, 16, 30), 0, 10);

    assertEquals(1, orders.getTotalPages());
    assertEquals(1, orders.getContent().size());
    assertEquals(1L, orders.getContent().getFirst().getStatus().getId());
    assertEquals(1L, orders.getContent().getFirst().getId());
    assertEquals(1, orders.getContent().getFirst().getItems().size());
    assertEquals(1L, orders.getContent().getFirst().getItems().getFirst().getId());
  }

  @Test
  public void
      shouldThrowIllegalArgumentExceptionWhenGettingFilteredOrdersWithoutOrderDateAfterAndOrderDateBefore() {
    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.getFilteredOrders(null, LocalDateTime.now(), null, 0, 10));
  }

  @Test
  public void
      shouldThrowIllegalArgumentExceptionWhenGettingFilteredOrdersWithoutOrderDateBeforeAndOrderDateAfter() {
    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.getFilteredOrders(null, null, LocalDateTime.now(), 0, 10));
  }

  @Test
  public void
      shouldThrowIllegalArgumentExceptionWhenGettingFilteredOrdersWithOrderDateAfterBeforeOrderDateBefore() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            orderService.getFilteredOrders(
                null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now(), 0, 10));
  }

  @Test
  public void shouldDeleteOrderById() {
    when(orderRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> orderService.deleteOrder(1L));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenOrderNotFoundOnDelete() {
    when(orderRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> orderService.deleteOrder(1L));
  }

  @Test
  public void shouldThrowDeletionNotAllowedExceptionWhenDeletingOrderWithDependencies() {
    when(orderRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(orderRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> orderService.deleteOrder(1L));
  }

  @Test
  public void shouldThrowExceptionWhenDataIntegrityViolationIsNotCausedByConstraintViolation() {
    when(orderRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(orderRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> orderService.deleteOrder(1L));
  }

  @Test
  public void shouldChangeOrderStatusAndReturnOrderId() {
    ChangeOrderStatusRequest changeOrderStatusRequest = new ChangeOrderStatusRequest(1L);
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
    when(orderStatusService.getOrderStatusById(1L)).thenReturn(Optional.of(orderStatus));

    Long orderId = orderService.changeOrderStatus(1L, changeOrderStatusRequest);

    assertEquals(1L, orderId);
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenOrderNotFoundOnChangeOrderStatus() {
    ChangeOrderStatusRequest changeOrderStatusRequest = new ChangeOrderStatusRequest(1L);

    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.changeOrderStatus(1L, changeOrderStatusRequest));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenOrderStatusNotFoundOnChangeOrderStatus() {
    ChangeOrderStatusRequest changeOrderStatusRequest = new ChangeOrderStatusRequest(1L);
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
    when(orderStatusService.getOrderStatusById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> orderService.changeOrderStatus(1L, changeOrderStatusRequest));
  }

  @Test
  public void shouldAddRatingToOrderAndReturnOrderId() {
    RatingRequest ratingRequest = new RatingRequest(null, 5, "Great");
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
    when(orderRepository.existsByRatingIsNotNullAndId(1L)).thenReturn(false);
    when(ratingMapper.toEntity(Rating.builder().rating(5).description("Great").build()))
        .thenReturn(RatingEntity.builder().rating(5).description("Great").build());

    Long orderId = orderService.addRatingToOrder(1L, ratingRequest);

    assertEquals(1L, orderId);
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenOrderNotFoundOnAddRating() {
    RatingRequest ratingRequest = new RatingRequest(null, 5, "Great");

    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> orderService.addRatingToOrder(1L, ratingRequest));
  }

  @Test
  public void shouldThrowConflictExceptionWhenOrderAlreadyRatedOnAddRating() {
    RatingRequest ratingRequest = new RatingRequest(null, 5, "Great");
    OrderEntity orderEntity =
        OrderEntity.builder()
            .id(1L)
            .orderDate(LocalDateTime.now())
            .statusChangeDate(LocalDateTime.now())
            .customer(userEntity)
            .statusChangeDate(LocalDateTime.now())
            .status(orderStatusEntity)
            .items(
                List.of(
                    OrderItemEntity.builder()
                        .id(1L)
                        .product(productEntity)
                        .quantity(1)
                        .price(BigDecimal.valueOf(1000.0))
                        .discount(BigDecimal.ZERO)
                        .build()))
            .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
    when(orderRepository.existsByRatingIsNotNullAndId(1L)).thenReturn(true);

    assertThrows(ConflictException.class, () -> orderService.addRatingToOrder(1L, ratingRequest));
  }
}
