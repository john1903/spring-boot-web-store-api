package me.jangluzniewicz.webstore.orders.controllers;

import jakarta.validation.Valid;
import java.nio.file.AccessDeniedException;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.orders.interfaces.IOrder;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.security.interfaces.ISecurity;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Order> getOrder(@PathVariable Long id) throws AccessDeniedException {
    Order order =
        orderService
            .getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    if (authService.getCurrentUser().hasRole("ADMIN")
        || order.getCustomer().getId().equals(authService.getCurrentUser().getId())) {
      return ResponseEntity.ok(order);
    } else {
      throw new AccessDeniedException("Access denied");
    }
  }

  @GetMapping("/current")
  public ResponseEntity<PagedResponse<Order>> getCurrentUserOrders(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    return ResponseEntity.ok(
        orderService.getOrdersByCustomerId(authService.getCurrentUser().getId(), page, size));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateOrder(
      @PathVariable Long id, @Valid @RequestBody OrderRequest orderRequest)
      throws AccessDeniedException {
    if (orderService.orderExists(id)) {
      if (authService.getCurrentUser().hasRole("ADMIN")
          || orderRequest.getCustomerId().equals(authService.getCurrentUser().getId())) {
        orderService.updateOrder(id, orderRequest);
        return ResponseEntity.noContent().build();
      } else {
        throw new AccessDeniedException("Access denied");
      }
    } else {
      throw new NotFoundException("Order with id " + id + " not found");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
