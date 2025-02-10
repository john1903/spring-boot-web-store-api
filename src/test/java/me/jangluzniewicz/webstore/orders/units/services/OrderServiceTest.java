package me.jangluzniewicz.webstore.orders.units.services;

import java.math.BigDecimal;
import java.util.List;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderItemRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.mappers.OrderMapper;
import me.jangluzniewicz.webstore.orders.repositories.OrderRepository;
import me.jangluzniewicz.webstore.orders.services.OrderService;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import org.junit.jupiter.api.BeforeEach;
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

  private OrderRequest orderRequest1;
  private OrderRequest orderRequest2;
  private OrderFilterRequest orderFilterRequest;

  @BeforeEach
  void setUp() {
    orderRequest1 =
        new OrderRequest(
            null,
            null,
            1L,
            null,
            null,
            List.of(new OrderItemRequest(null, 1L, 1, BigDecimal.valueOf(1000.0), null)));
    orderRequest2 =
        new OrderRequest(
            null,
            null,
            1L,
            null,
            null,
            List.of(new OrderItemRequest(null, 1L, 3, BigDecimal.valueOf(1000.0), null)));
  }
}
