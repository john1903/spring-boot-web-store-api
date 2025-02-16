package me.jangluzniewicz.webstore.orders.controllers;

import jakarta.validation.Valid;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.orders.interfaces.IOrder;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.security.interfaces.ISecurity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping("/orders")
public class OrderController {
  private final IOrder orderService;
  private final ISecurity authService;

  public OrderController(IOrder orderService, ISecurity authService) {
    this.orderService = orderService;
    this.authService = authService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<PagedResponse<Order>> getOrders(
      @Valid @ModelAttribute OrderFilterRequest filterRequest,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    return ResponseEntity.ok(orderService.getFilteredOrders(filterRequest, page, size));
  }

  @GetMapping("/{id}")
  @PostAuthorize("hasRole('ADMIN') or returnObject.body.customer.id == authentication.principal.id")
  public ResponseEntity<Order> getOrder(@PathVariable Long id) {
    Order order =
        orderService
            .getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    return ResponseEntity.ok(order);
  }

  @GetMapping("/current")
  public ResponseEntity<PagedResponse<Order>> getCurrentUserOrders(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    return ResponseEntity.ok(
        orderService.getOrdersByCustomerId(authService.getCurrentUser().getId(), page, size));
  }

  @PreAuthorize(
      "hasRole('ADMIN') or @orderService.getOrderOwnerId(#id) == authentication.principal.id")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateOrder(
      @PathVariable Long id, @Valid @RequestBody OrderRequest orderRequest) {
    orderService.updateOrder(id, orderRequest);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
