package me.jangluzniewicz.webstore.orderstatuses.controllers;

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
import me.jangluzniewicz.webstore.orderstatuses.interfaces.IOrderStatus;
import me.jangluzniewicz.webstore.orderstatuses.models.OrderStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order Statuses", description = "Operations related to order statuses")
@RestController
@RequestMapping("/order-statuses")
public class OrderStatusController {
  private final IOrderStatus orderStatusService;

  public OrderStatusController(IOrderStatus orderStatusService) {
    this.orderStatusService = orderStatusService;
  }

  @Operation(
      summary = "Get order statuses",
      description = "Returns a paginated list of order statuses")
  @ApiResponse(
      responseCode = "200",
      description = "List of order statuses",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PagedResponse.class)))
  @GetMapping
  public ResponseEntity<PagedResponse<OrderStatus>> getOrderStatuses(
      @Parameter(in = ParameterIn.QUERY, description = "Page number", example = "0")
          @RequestParam(defaultValue = "0")
          Integer page,
      @Parameter(in = ParameterIn.QUERY, description = "Page size", example = "20")
          @RequestParam(defaultValue = "20")
          Integer size) {
    return ResponseEntity.ok(orderStatusService.getAllOrderStatuses(page, size));
  }

  @Operation(
      summary = "Get order status by ID",
      description = "Returns an order status based on the provided ID")
  @ApiResponse(
      responseCode = "200",
      description = "Order status found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = OrderStatus.class)))
  @ApiResponse(responseCode = "404", description = "Order status not found", content = @Content)
  @GetMapping("/{id}")
  public ResponseEntity<OrderStatus> getOrderStatus(
      @Parameter(
              in = ParameterIn.PATH,
              description = "Order status ID",
              required = true,
              example = "1")
          @PathVariable
          Long id) {
    return orderStatusService
        .getOrderStatusById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new NotFoundException("Order status not found"));
  }

  @Operation(
      summary = "Create order status",
      description = "Creates a new order status (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "201",
      description = "Order status created",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = IdResponse.class)))
  @ApiResponse(
      responseCode = "409",
      description = "Order status name already exists",
      content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<IdResponse> createOrderStatus(
      @RequestBody(
              description = "Order status creation payload",
              required = true,
              content = @Content(schema = @Schema(implementation = OrderStatusRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          OrderStatusRequest orderStatusRequest) {
    IdResponse response = orderStatusService.createNewOrderStatus(orderStatusRequest);
    return ResponseEntity.created(URI.create("/order-statuses/" + response.getId())).body(response);
  }

  @Operation(
      summary = "Update order status",
      description = "Updates an existing order status (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Order status updated", content = @Content)
  @ApiResponse(responseCode = "404", description = "Order status not found", content = @Content)
  @ApiResponse(
      responseCode = "409",
      description = "Order status name already exists",
      content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateOrderStatus(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the order status to update",
              required = true,
              example = "1")
          @PathVariable
          Long id,
      @RequestBody(
              description = "Order status update payload",
              required = true,
              content = @Content(schema = @Schema(implementation = OrderStatusRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          OrderStatusRequest orderStatusRequest) {
    orderStatusService.updateOrderStatus(id, orderStatusRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Delete order status",
      description = "Deletes an order status by its ID (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Order status deleted", content = @Content)
  @ApiResponse(responseCode = "404", description = "Order status not found", content = @Content)
  @ApiResponse(
      responseCode = "409",
      description = "Order status has associated orders",
      content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrderStatus(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the order status to delete",
              required = true,
              example = "1")
          @PathVariable
          Long id) {
    orderStatusService.deleteOrderStatus(id);
    return ResponseEntity.noContent().build();
  }
}
