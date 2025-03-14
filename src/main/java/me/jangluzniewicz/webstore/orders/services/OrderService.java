package me.jangluzniewicz.webstore.orders.services;

import jakarta.transaction.Transactional;
import java.util.Optional;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.OrderStatusNotAllowedException;
import me.jangluzniewicz.webstore.orders.controllers.*;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.interfaces.IOrder;
import me.jangluzniewicz.webstore.orders.mappers.OrderMapper;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.orders.models.OrderItem;
import me.jangluzniewicz.webstore.orders.models.Rating;
import me.jangluzniewicz.webstore.orders.repositories.OrderRepository;
import me.jangluzniewicz.webstore.orders.repositories.OrderSpecification;
import me.jangluzniewicz.webstore.orderstatuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class OrderService implements IOrder {
  private final OrderRepository orderRepository;
  private final IOrderStatus orderStatusService;
  private final IUser userService;
  private final IProduct productService;
  private final OrderMapper orderMapper;
  private static final Long ORDER_STATUS_COMPLETED_ID = 4L;
  private static final Long ORDER_STATUS_CANCELLED_ID = 3L;

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
  public IdResponse createNewOrder(OrderRequest orderRequest) {
    Order order =
        Order.builder()
            .customer(
                orderRequest.getCustomerId() != null
                    ? userService
                        .getUserById(orderRequest.getCustomerId())
                        .orElseThrow(
                            () ->
                                new NotFoundException(
                                    "User with id " + orderRequest.getCustomerId() + " not found"))
                    : userService
                        .getUserByEmail("GUEST")
                        .orElseThrow(() -> new NotFoundException("Guest user not found")))
            .email(orderRequest.getEmail())
            .phoneNumber(orderRequest.getPhoneNumber())
            .items(
                orderRequest.getItems().stream()
                    .map(
                        orderItemRequest -> {
                          Product product =
                              productService
                                  .getProductById(orderItemRequest.getProductId())
                                  .orElseThrow(
                                      () ->
                                          new NotFoundException(
                                              "Product with id "
                                                  + orderItemRequest.getProductId()
                                                  + " not found"));
                          return OrderItem.builder()
                              .product(product)
                              .price(product.getPrice())
                              .quantity(orderItemRequest.getQuantity())
                              .build();
                        })
                    .toList())
            .build();
    return new IdResponse(orderRepository.save(orderMapper.toEntity(order)).getId());
  }

  @Override
  public Optional<Order> getOrderById(Long id) {
    return orderRepository.findById(id).map(orderMapper::fromEntity);
  }

  @Override
  public PagedResponse<Order> getOrdersByCustomerId(Long customerId, Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Order> orders =
        orderRepository
            .findAllByCustomerIdOrderByOrderDateAscIdAsc(customerId, pageable)
            .map(orderMapper::fromEntity);
    return new PagedResponse<>(orders.getTotalPages(), orders.toList());
  }

  @Override
  public PagedResponse<Order> getAllOrders(Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Order> orders = orderRepository.findAll(pageable).map(orderMapper::fromEntity);
    return new PagedResponse<>(orders.getTotalPages(), orders.toList());
  }

  @Override
  public PagedResponse<Order> getFilteredOrders(
      OrderFilterRequest filter, Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Specification<OrderEntity> specification = OrderSpecification.filterBy(filter);
    Page<Order> orders =
        orderRepository.findAll(specification, pageable).map(orderMapper::fromEntity);
    return new PagedResponse<>(orders.getTotalPages(), orders.toList());
  }

  @Override
  @Transactional
  public void updateOrder(Long id, OrderRequest orderRequest) {
    Order order =
        getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    order.setCustomer(
        userService
            .getUserById(orderRequest.getCustomerId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "User with id " + orderRequest.getCustomerId() + " not found")));
    order.setItems(
        orderRequest.getItems().stream()
            .map(
                orderItemRequest -> {
                  Product product =
                      productService
                          .getProductById(orderItemRequest.getProductId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "Product with id "
                                          + orderItemRequest.getProductId()
                                          + " not found"));
                  return OrderItem.builder()
                      .id(orderItemRequest.getId())
                      .product(product)
                      .price(product.getPrice())
                      .quantity(orderItemRequest.getQuantity())
                      .build();
                })
            .toList());
    orderRepository.save(orderMapper.toEntity(order));
  }

  @Override
  @Transactional
  public void changeOrderStatus(Long id, ChangeOrderStatusRequest changeOrderStatusRequest) {
    Order order =
        getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    if (orderStatusCannotBeChanged(order)) {
      throw new OrderStatusNotAllowedException(
          "Order status for order with id " + id + " cannot be changed");
    }
    order.setStatus(
        orderStatusService
            .getOrderStatusById(changeOrderStatusRequest.getOrderStatusId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Order status with id "
                            + changeOrderStatusRequest.getOrderStatusId()
                            + " not found")));
    orderRepository.save(orderMapper.toEntity(order));
  }

  @Override
  @Transactional
  public void addRatingToOrder(Long id, RatingRequest ratingRequest) {
    Order order =
        getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    if (!order.getStatus().getId().equals(ORDER_STATUS_COMPLETED_ID)) {
      throw new ConflictException("Order with id " + id + " must be completed to add rating");
    }
    if (order.getRating() != null) {
      throw new ConflictException("Rating for order with id " + id + " already exists");
    }
    order.setRating(
        Rating.builder()
            .rating(ratingRequest.getRating())
            .description(ratingRequest.getDescription())
            .build());
    orderRepository.save(orderMapper.toEntity(order));
  }

  @Override
  @Transactional
  public void deleteOrder(Long id) {
    if (!orderRepository.existsById(id)) {
      throw new NotFoundException("Order with id " + id + " not found");
    }
    orderRepository.deleteById(id);
  }

  public Long getOrderOwnerId(Long id) {
    return orderRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"))
        .getCustomer()
        .getId();
  }

  private boolean orderStatusCannotBeChanged(Order order) {
    Long currentStatusId = order.getStatus().getId();
    return currentStatusId.equals(ORDER_STATUS_COMPLETED_ID)
        || currentStatusId.equals(ORDER_STATUS_CANCELLED_ID);
  }
}
