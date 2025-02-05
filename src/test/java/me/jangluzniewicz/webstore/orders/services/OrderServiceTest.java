package me.jangluzniewicz.webstore.orders.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.order_statuses.mappers.OrderStatusMapper;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.orders.controllers.OrderItemRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.entities.OrderItemEntity;
import me.jangluzniewicz.webstore.orders.mappers.OrderItemMapper;
import me.jangluzniewicz.webstore.orders.mappers.OrderMapper;
import me.jangluzniewicz.webstore.orders.mappers.RatingMapper;
import me.jangluzniewicz.webstore.orders.repositories.OrderRepository;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
import me.jangluzniewicz.webstore.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
