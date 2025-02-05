package me.jangluzniewicz.webstore.orders.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.order_statuses.mappers.OrderStatusMapper;
import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.interfaces.IOrder;
import me.jangluzniewicz.webstore.orders.mappers.OrderItemMapper;
import me.jangluzniewicz.webstore.orders.mappers.OrderMapper;
import me.jangluzniewicz.webstore.orders.mappers.RatingMapper;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.orders.models.OrderItem;
import me.jangluzniewicz.webstore.orders.models.Rating;
import me.jangluzniewicz.webstore.orders.repositories.OrderRepository;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import me.jangluzniewicz.webstore.users.mappers.UserMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements IOrder {
  private final OrderRepository orderRepository;
  private final IOrderStatus orderStatusService;
  private final IUser userService;
  private final IProduct productService;
  private final OrderMapper orderMapper;
  private final UserMapper userMapper;
  private final OrderStatusMapper orderStatusMapper;
  private final OrderItemMapper orderItemMapper;
  private final RatingMapper ratingMapper;

  public OrderService(
      OrderRepository orderRepository,
      IUser userService,
      IProduct productService,
      IOrderStatus orderStatusService,
      OrderMapper orderMapper,
      UserMapper userMapper,
      OrderStatusMapper orderStatusMapper,
      OrderItemMapper orderItemMapper,
      RatingMapper ratingMapper) {
    this.orderRepository = orderRepository;
    this.userService = userService;
    this.productService = productService;
    this.orderStatusService = orderStatusService;
    this.orderMapper = orderMapper;
    this.userMapper = userMapper;
    this.orderStatusMapper = orderStatusMapper;
    this.orderItemMapper = orderItemMapper;
    this.ratingMapper = ratingMapper;
  }

  @Override
  @Transactional
  public Long createNewOrder(@NotNull OrderRequest orderRequest) {
    Order order =
        Order.builder()
            .customer(
                userService
                    .getUserById(orderRequest.getCustomerId())
                    .orElseThrow(
                        () ->
                            new NotFoundException(
                                "User with id " + orderRequest.getCustomerId() + " not found")))
            .items(
                orderRequest.getItems().stream()
                    .map(
                        orderItemRequest ->
                            OrderItem.builder()
                                .product(
                                    productService
                                        .getProductById(orderItemRequest.getProductId())
                                        .orElseThrow(
                                            () ->
                                                new NotFoundException(
                                                    "Product with id "
                                                        + orderItemRequest.getProductId()
                                                        + " not found")))
                                .price(orderItemRequest.getPrice())
                                .quantity(orderItemRequest.getQuantity())
                                .discount(orderItemRequest.getDiscount())
                                .build())
                    .toList())
            .build();
    return orderRepository.save(orderMapper.toEntity(order)).getId();
  }

  @Override
  public Optional<Order> getOrderById(@NotNull @Min(1) Long id) {
    return orderRepository.findById(id).map(orderMapper::fromEntity);
  }

  @Override
  public PagedResponse<Order> getOrdersByCustomerId(
      @NotNull @Min(1) Long customerId,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Order> orders =
        orderRepository
            .findAllByCustomerIdOrderByOrderDateAscIdAsc(customerId, pageable)
            .map(orderMapper::fromEntity);
    return new PagedResponse<>(orders.getTotalPages(), orders.toList());
  }

  @Override
  public PagedResponse<Order> getAllOrders(
      @NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Order> orders = orderRepository.findAll(pageable).map(orderMapper::fromEntity);
    return new PagedResponse<>(orders.getTotalPages(), orders.toList());
  }

  @Override
  public PagedResponse<Order> getFilteredOrders(
      @Min(1) Long statusId,
      LocalDateTime orderDateAfter,
      LocalDateTime orderDateBefore,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Order> orders;
    if (statusId != null && orderDateAfter != null && orderDateBefore != null) {
      orders =
          orderRepository
              .findAllByStatusIdAndOrderDateBetweenOrderByOrderDateAscIdAsc(
                  statusId, orderDateAfter, orderDateBefore, pageable)
              .map(orderMapper::fromEntity);
    } else if (statusId != null) {
      orders =
          orderRepository
              .findAllByStatusIdOrderByOrderDateAscIdAsc(statusId, pageable)
              .map(orderMapper::fromEntity);
    } else if (orderDateAfter != null && orderDateBefore != null) {
      orders =
          orderRepository
              .findAllByOrderDateBetweenOrderByOrderDateAscIdAsc(
                  orderDateAfter, orderDateBefore, pageable)
              .map(orderMapper::fromEntity);
    } else if (orderDateAfter != null || orderDateBefore != null) {
      throw new IllegalArgumentException(
          "Both orderDateAfter and orderDateBefore must be specified");
    } else {
      return getAllOrders(page, size);
    }
    return new PagedResponse<>(orders.getTotalPages(), orders.toList());
  }

  @Override
  @Transactional
  public Long updateOrder(@NotNull @Min(1) Long id, @NotNull OrderRequest orderRequest) {
    OrderEntity orderEntity =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    orderEntity.setOrderDate(orderRequest.getOrderDate());
    orderEntity.setStatusChangeDate(orderRequest.getStatusChangeDate());
    orderEntity.setCustomer(
        userMapper.toEntity(
            userService
                .getUserById(orderRequest.getCustomerId())
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            "User with id " + orderRequest.getCustomerId() + " not found"))));
    orderEntity.setStatus(
        orderStatusMapper.toEntity(
            orderStatusService
                .getOrderStatusById(orderRequest.getStatusId())
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            "Order status with id " + orderRequest.getStatusId() + " not found"))));
    orderEntity.setRating(
        ratingMapper.toEntity(
            Rating.builder()
                .id(orderRequest.getRating().getId())
                .rating(orderRequest.getRating().getRating())
                .description(orderRequest.getRating().getDescription())
                .build()));
    orderEntity.setItems(
        orderRequest.getItems().stream()
            .map(
                orderItemRequest ->
                    OrderItem.builder()
                        .id(orderItemRequest.getId())
                        .product(
                            productService
                                .getProductById(orderItemRequest.getProductId())
                                .orElseThrow(
                                    () ->
                                        new NotFoundException(
                                            "Product with id "
                                                + orderItemRequest.getProductId()
                                                + " not found")))
                        .quantity(orderItemRequest.getQuantity())
                        .discount(orderItemRequest.getDiscount())
                        .build())
            .map(orderItemMapper::toEntity)
            .toList());
    return orderEntity.getId();
  }

  @Override
  @Transactional
  public Long changeOrderStatus(
      @NotNull @Min(1) Long id, @NotNull ChangeOrderStatusRequest changeOrderStatusRequest) {
    OrderEntity orderEntity =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    orderEntity.setStatus(
        orderStatusMapper.toEntity(
            orderStatusService
                .getOrderStatusById(changeOrderStatusRequest.getOrderStatusId())
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            "Order status with id "
                                + changeOrderStatusRequest.getOrderStatusId()
                                + " not found"))));
    orderEntity.setStatusChangeDate(LocalDateTime.now());
    return orderEntity.getId();
  }

  @Override
  @Transactional
  public Long addRatingToOrder(@NotNull @Min(1) Long id, @NotNull RatingRequest ratingRequest) {
    OrderEntity orderEntity =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    if (orderRepository.existsByRatingIsNotNullAndId(id)) {
      throw new ConflictException("Rating for order with id " + id + " already exists");
    }
    orderEntity.setRating(
        ratingMapper.toEntity(
            Rating.builder()
                .rating(ratingRequest.getRating())
                .description(ratingRequest.getDescription())
                .build()));
    return orderEntity.getId();
  }

  @Override
  @Transactional
  public void deleteOrder(@NotNull @Min(1) Long id) {
    if (!orderRepository.existsById(id)) {
      throw new NotFoundException("Order with id " + id + " not found");
    }
    try {
      orderRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException) {
        throw new DeletionNotAllowedException(
            "Product with id " + id + " cannot be deleted due to existing relations");
      } else {
        throw e;
      }
    }
  }
}
