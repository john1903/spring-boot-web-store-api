package me.jangluzniewicz.webstore.orderstatuses.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.orderstatuses.controllers.OrderStatusRequest;
import me.jangluzniewicz.webstore.orderstatuses.models.OrderStatus;

/** Interface for managing order statuses. */
public interface IOrderStatus {

  /**
   * Creates a new order status.
   *
   * @param orderStatusRequest the request object containing the details of the order status to be
   *     created; must not be null.
   * @return an {@link IdResponse} containing the ID of the newly created order status.
   */
  IdResponse createNewOrderStatus(@NotNull OrderStatusRequest orderStatusRequest);

  /**
   * Retrieves an order status by its ID.
   *
   * @param id the ID of the order status to be retrieved; must be a positive number.
   * @return an {@link Optional} containing the {@link OrderStatus} if found, or empty if not found.
   */
  Optional<OrderStatus> getOrderStatusById(@NotNull @Min(1) Long id);

  /**
   * Retrieves all order statuses with pagination.
   *
   * @param page the page number to retrieve; must be a non-negative number.
   * @param size the number of order statuses per page; must be a positive number.
   * @return a {@link PagedResponse} containing the paginated list of order statuses.
   */
  PagedResponse<OrderStatus> getAllOrderStatuses(
      @NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  /**
   * Updates an existing order status.
   *
   * @param id the ID of the order status to be updated; must be a positive number.
   * @param orderStatusRequest the request object containing the updated details of the order
   *     status; must not be null.
   */
  void updateOrderStatus(@NotNull @Min(1) Long id, @NotNull OrderStatusRequest orderStatusRequest);

  /**
   * Deletes an order status by its ID.
   *
   * @param id the ID of the order status to be deleted; must be a positive number.
   */
  void deleteOrderStatus(@NotNull @Min(1) Long id);
}
