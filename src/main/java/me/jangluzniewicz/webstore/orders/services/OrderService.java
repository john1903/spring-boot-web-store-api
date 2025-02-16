package me.jangluzniewicz.webstore.orders.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.interfaces.IOrder;
import me.jangluzniewicz.webstore.orders.mappers.OrderMapper;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.orders.models.OrderItem;
import me.jangluzniewicz.webstore.orders.models.Rating;
import me.jangluzniewicz.webstore.orders.repositories.OrderRepository;
import me.jangluzniewicz.webstore.orders.repositories.OrderSpecification;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements IOrder {
  private final OrderRepository orderRepository;
  private final IOrderStatus orderStatusService;
  private final IUser userService;
  private final IProduct productService;
  private final OrderMapper orderMapper;

  public OrderService(
      OrderRepository orderRepository,
      IUser userService,
      IProduct productService,
      IOrderStatus orderStatusService,
      OrderMapper orderMapper) {
    this.orderRepository = orderRepository;
    this.userService = userService;
    this.productService = productService;
    this.orderStatusService = orderStatusService;
    this.orderMapper = orderMapper;
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
      @NotNull OrderFilterRequest filter,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Specification<OrderEntity> specification = OrderSpecification.filterBy(filter);
    Page<Order> orders =
        orderRepository.findAll(specification, pageable).map(orderMapper::fromEntity);
    return new PagedResponse<>(orders.getTotalPages(), orders.toList());
  }

  @Override
  @Transactional
  public Long updateOrder(@NotNull @Min(1) Long id, @NotNull OrderRequest orderRequest) {
    Order order =
        getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    order.setOrderDate(orderRequest.getOrderDate());
    order.setStatusChangeDate(orderRequest.getStatusChangeDate());
    order.setCustomer(
        userService
            .getUserById(orderRequest.getCustomerId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "User with id " + orderRequest.getCustomerId() + " not found")));
    order.setStatus(
        orderRequest.getStatusId() != null
            ? orderStatusService
                .getOrderStatusById(orderRequest.getStatusId())
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            "Order status with id " + orderRequest.getStatusId() + " not found"))
            : null);
    order.setRating(
        orderRequest.getRating() != null
            ? Rating.builder()
                .id(orderRequest.getRating().getId())
                .rating(orderRequest.getRating().getRating())
                .description(orderRequest.getRating().getDescription())
                .build()
            : null);
    order.setItems(
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
                        .price(orderItemRequest.getPrice())
                        .quantity(orderItemRequest.getQuantity())
                        .discount(orderItemRequest.getDiscount())
                        .build())
            .toList());
    return orderRepository.save(orderMapper.toEntity(order)).getId();
  }

  @Override
  @Transactional
  public Long changeOrderStatus(
      @NotNull @Min(1) Long id, @NotNull ChangeOrderStatusRequest changeOrderStatusRequest) {
    Order order =
        getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    order.setStatus(
        orderStatusService
            .getOrderStatusById(changeOrderStatusRequest.getOrderStatusId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Order status with id "
                            + changeOrderStatusRequest.getOrderStatusId()
                            + " not found")));
    return orderRepository.save(orderMapper.toEntity(order)).getId();
  }

  @Override
  @Transactional
  public Long addRatingToOrder(@NotNull @Min(1) Long id, @NotNull RatingRequest ratingRequest) {
    Order order =
        getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    if (order.getRating() != null) {
      throw new ConflictException("Rating for order with id " + id + " already exists");
    }
    order.setRating(
        Rating.builder()
            .rating(ratingRequest.getRating())
            .description(ratingRequest.getDescription())
            .build());
    return orderRepository.save(orderMapper.toEntity(order)).getId();
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

  @Override
  public boolean orderExists(Long id) {
    return orderRepository.existsById(id);
  }

  public Long getOrderOwnerId(Long id) {
    Order order =
        getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    return order.getCustomer().getId();
  }
}
