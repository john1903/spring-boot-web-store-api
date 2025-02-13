package me.jangluzniewicz.webstore.order_statuses.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.order_statuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/order-statuses")
public class OrderStatusController {
  private final IOrderStatus orderStatusService;

  public OrderStatusController(IOrderStatus orderStatusService) {
    this.orderStatusService = orderStatusService;
  }

  @GetMapping
  public ResponseEntity<PagedResponse<OrderStatus>> getOrderStatuses(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    return ResponseEntity.ok(orderStatusService.getAllOrderStatuses(page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderStatus> getOrderStatus(@PathVariable Long id) {
    return orderStatusService
        .getOrderStatusById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Void> createOrderStatus(
      @Valid @RequestBody OrderStatusRequest orderStatusRequest) {
    Long orderStatusId = orderStatusService.createNewOrderStatus(orderStatusRequest);
    return ResponseEntity.created(URI.create("/order-statuses/" + orderStatusId)).build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateOrderStatus(
      @PathVariable Long id, @Valid @RequestBody OrderStatusRequest orderStatusRequest) {
    orderStatusService.updateOrderStatus(id, orderStatusRequest);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrderStatus(@PathVariable Long id) {
    orderStatusService.deleteOrderStatus(id);
    return ResponseEntity.noContent().build();
  }
}
