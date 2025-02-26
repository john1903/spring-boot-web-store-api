package me.jangluzniewicz.webstore.orders.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.orders.interfaces.IOrder;
import me.jangluzniewicz.webstore.orders.models.Order;
import me.jangluzniewicz.webstore.security.interfaces.ISecurity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders", description = "Operations related to orders")
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/orders")
public class OrderController {
  private final IOrder orderService;
  private final ISecurity authService;

  public OrderController(IOrder orderService, ISecurity authService) {
    this.orderService = orderService;
    this.authService = authService;
  }

  @Operation(
      summary = "Get orders",
      description = "Returns a paginated list of orders filtered by provided criteria (ADMIN only)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "200",
      description = "List of filtered orders",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PagedResponse.class)))
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<PagedResponse<Order>> getOrders(
      @Valid @ModelAttribute OrderFilterRequest filterRequest,
      @Parameter(in = ParameterIn.QUERY, description = "Page number", example = "0")
          @RequestParam(defaultValue = "0")
          Integer page,
      @Parameter(in = ParameterIn.QUERY, description = "Page size", example = "20")
          @RequestParam(defaultValue = "20")
          Integer size) {
    return ResponseEntity.ok(orderService.getFilteredOrders(filterRequest, page, size));
  }

  @Operation(
      summary = "Get order by ID",
      description = "Returns an order by its ID. ADMIN or owner can access the order",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "200",
      description = "Order found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Order.class)))
  @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
  @GetMapping("/{id}")
  @PostAuthorize("hasRole('ADMIN') or returnObject.body.customer.id == authentication.principal.id")
  public ResponseEntity<Order> getOrder(
      @Parameter(in = ParameterIn.PATH, description = "Order ID", required = true, example = "1")
          @PathVariable
          Long id) {
    Order order =
        orderService
            .getOrderById(id)
            .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    return ResponseEntity.ok(order);
  }

  @Operation(
      summary = "Get current user's orders",
      description = "Returns a paginated list of orders for the current authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "200",
      description = "List of user's orders",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PagedResponse.class)))
  @GetMapping("/current")
  public ResponseEntity<PagedResponse<Order>> getCurrentUserOrders(
      @Parameter(in = ParameterIn.QUERY, description = "Page number", example = "0")
          @RequestParam(defaultValue = "0")
          Integer page,
      @Parameter(in = ParameterIn.QUERY, description = "Page size", example = "20")
          @RequestParam(defaultValue = "20")
          Integer size) {
    return ResponseEntity.ok(
        orderService.getOrdersByCustomerId(authService.getCurrentUser().getId(), page, size));
  }

  @Operation(
      summary = "Create order",
      description = "Creates a new order. ADMIN or customer (owner) can create an order",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "201",
      description = "Order created",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = IdResponse.class)))
  @PreAuthorize("hasRole('ADMIN') or #orderRequest.customerId == authentication.principal.id")
  @PostMapping
  public ResponseEntity<IdResponse> createOrder(
      @RequestBody(
              description = "Order creation payload",
              required = true,
              content = @Content(schema = @Schema(implementation = OrderRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          OrderRequest orderRequest) {
    IdResponse response = orderService.createNewOrder(orderRequest);
    return ResponseEntity.created(URI.create("/orders/" + response.getId())).body(response);
  }

  @Operation(
      summary = "Update order",
      description = "Updates an existing order (ADMIN only)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Order updated", content = @Content)
  @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateOrder(
      @Parameter(in = ParameterIn.PATH, description = "Order ID", required = true, example = "1")
          @PathVariable
          Long id,
      @RequestBody(
              description = "Order update payload",
              required = true,
              content = @Content(schema = @Schema(implementation = OrderRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          OrderRequest orderRequest) {
    orderService.updateOrder(id, orderRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Update order status",
      description = "Updates the status of an order (ADMIN only)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Order status updated", content = @Content)
  @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
  @ApiResponse(
      responseCode = "409",
      description = "Order status change not allowed",
      content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}/status")
  public ResponseEntity<Void> updateOrderStatus(
      @Parameter(in = ParameterIn.PATH, description = "Order ID", required = true, example = "1")
          @PathVariable
          Long id,
      @RequestBody(
              description = "Order status update payload",
              required = true,
              content = @Content(schema = @Schema(implementation = OrderStatusRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          OrderStatusRequest orderStatusRequest) {
    orderService.changeOrderStatus(id, orderStatusRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Rate order",
      description = "Adds a rating to an order. ADMIN or order owner can rate the order",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Order rated", content = @Content)
  @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
  @ApiResponse(
      responseCode = "409",
      description = "Order already rated or not completed",
      content = @Content)
  @PreAuthorize(
      "hasRole('ADMIN') or @orderService.getOrderOwnerId(#id) == authentication.principal.id")
  @PostMapping("/{id}/rating")
  public ResponseEntity<Void> rateOrder(
      @Parameter(in = ParameterIn.PATH, description = "Order ID", required = true, example = "1")
          @PathVariable
          Long id,
      @RequestBody(
              description = "Order rating payload",
              required = true,
              content = @Content(schema = @Schema(implementation = RatingRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          RatingRequest ratingRequest) {
    orderService.addRatingToOrder(id, ratingRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Delete order",
      description = "Deletes an order by its ID (ADMIN only)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Order deleted", content = @Content)
  @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
  @ApiResponse(responseCode = "409", description = "Order has associations", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(
      @Parameter(in = ParameterIn.PATH, description = "Order ID", required = true, example = "1")
          @PathVariable
          Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
