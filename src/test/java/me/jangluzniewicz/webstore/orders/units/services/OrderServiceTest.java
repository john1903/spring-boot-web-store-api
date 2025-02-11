package me.jangluzniewicz.webstore.orders.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.testdata.order_statuses.OrderStatusTestDataBuilder;
import me.jangluzniewicz.webstore.common.testdata.orders.*;
import me.jangluzniewicz.webstore.common.testdata.products.ProductTestDataBuilder;
import me.jangluzniewicz.webstore.common.testdata.users.UserTestDataBuilder;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.mappers.OrderMapper;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.orders.repositories.OrderRepository;
import me.jangluzniewicz.webstore.orders.services.OrderService;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
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
  @InjectMocks private OrderService orderService;

  private User user;
  private OrderStatus orderStatus;
  private Product product;
  private OrderEntity orderEntity;
  private Order order;
  private OrderRequest orderRequest1;
  private OrderRequest orderRequest2;
  private OrderFilterRequest orderFilterRequest;

  @BeforeEach
  void setUp() {
    user = UserTestDataBuilder.builder().id(1L).build().buildUser();
    orderStatus = OrderStatusTestDataBuilder.builder().id(1L).build().buildOrderStatus();
    product = ProductTestDataBuilder.builder().id(1L).build().buildProduct();
    orderEntity = OrderEntityTestDataBuilder.builder().id(1L).build().buildOrderEntity();
    order = OrderTestDataBuilder.builder().id(1L).build().buildOrder();
    orderRequest1 =
        OrderRequestTestDataBuilder.builder()
            .statusId(orderStatus.getId())
            .customerId(user.getId())
            .items(
                List.of(
                    OrderItemRequestTestDataBuilder.builder().productId(product.getId()).build()))
            .build()
            .buildOrderRequest();
    orderRequest2 =
        OrderRequestTestDataBuilder.builder()
            .statusId(orderStatus.getId())
            .customerId(user.getId())
            .items(
                List.of(
                    OrderItemRequestTestDataBuilder.builder()
                        .productId(product.getId())
                        .quantity(3)
                        .build()))
            .build()
            .buildOrderRequest();
    orderFilterRequest =
        OrderFilterRequestTestDataBuilder.builder()
            .statusId(orderStatus.getId())
            .build()
            .buildOrderFilterRequest();
  }

  @Test
  void createNewOrder_whenUserExistsAndProductExists_thenReturnOrderId() {
    when(userService.getUserById(orderRequest1.getCustomerId())).thenReturn(Optional.of(user));
    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
    when(orderRepository.save(any())).thenReturn(orderEntity);

    assertEquals(orderEntity.getId(), orderService.createNewOrder(orderRequest1));
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
}
