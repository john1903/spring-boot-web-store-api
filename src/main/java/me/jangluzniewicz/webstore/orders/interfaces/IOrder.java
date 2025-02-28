package me.jangluzniewicz.webstore.orders.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.orders.controllers.ChangeOrderStatusRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;
import me.jangluzniewicz.webstore.orders.controllers.OrderRequest;
import me.jangluzniewicz.webstore.orders.controllers.RatingRequest;
import me.jangluzniewicz.webstore.orders.models.Order;

/** Interface for managing orders. */
public interface IOrder {

  /**
   * Creates a new order.
   *
   * @param orderRequest the request object containing the details of the order to be created; must
   *     not be null.
   * @return an {@link IdResponse} containing the ID of the newly created order.
   */
  IdResponse createNewOrder(@NotNull OrderRequest orderRequest);

  /**
   * Retrieves an order by its ID.
   *
   * @param id the ID of the order to be retrieved; must be a positive number.
   * @return an {@link Optional} containing the {@link Order} if found, or empty if not found.
   */
  Optional<Order> getOrderById(@NotNull @Min(1) Long id);

  /**
   * Retrieves orders by customer ID with pagination.
   *
   * @param customerId the ID of the customer whose orders are to be retrieved; must be a positive
   *     number.
   * @param page the page number to retrieve; must be a non-negative number.
   * @param size the number of orders per page; must be a positive number.
   * @return a {@link PagedResponse} containing the paginated list of orders.
   */
  PagedResponse<Order> getOrdersByCustomerId(
      @NotNull @Min(1) Long customerId,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size);

  /**
   * Retrieves all orders with pagination.
   *
   * @param page the page number to retrieve; must be a non-negative number.
   * @param size the number of orders per page; must be a positive number.
   * @return a {@link PagedResponse} containing the paginated list of orders.
   */
  PagedResponse<Order> getAllOrders(@NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  /**
   * Retrieves filtered orders with pagination.
   *
   * @param filter the filter criteria for retrieving orders; must not be null.
   * @param page the page number to retrieve; must be a non-negative number.
   * @param size the number of orders per page; must be a positive number.
   * @return a {@link PagedResponse} containing the paginated list of filtered orders.
   */
  PagedResponse<Order> getFilteredOrders(
      @NotNull OrderFilterRequest filter,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size);

  /**
   * Updates an existing order.
   *
   * @param id the ID of the order to be updated; must be a positive number.
   * @param orderRequest the request object containing the updated details of the order; must not be
   *     null.
   */
  void updateOrder(@NotNull @Min(1) Long id, @NotNull OrderRequest orderRequest);

  /**
   * Changes the status of an existing order.
   *
   * @param id the ID of the order whose status is to be changed; must be a positive number.
   * @param changeOrderStatusRequest the request object containing the new status of the order; must
   *     not be null.
   */
  void changeOrderStatus(
      @NotNull @Min(1) Long id, @NotNull ChangeOrderStatusRequest changeOrderStatusRequest);

  /**
   * Adds a rating to an existing order.
   *
   * @param id the ID of the order to be rated; must be a positive number.
   * @param ratingRequest the request object containing the rating details; must not be null.
   */
  void addRatingToOrder(@NotNull @Min(1) Long id, @NotNull RatingRequest ratingRequest);

  /**
   * Retrieves the owner ID of an order.
   *
   * @param id the ID of the order whose owner ID is to be retrieved; must be a positive number.
   * @return the ID of the owner of the order.
   */
  Long getOrderOwnerId(@NotNull @Min(1) Long id);

  /**
   * Deletes an order by its ID.
   *
   * @param id the ID of the order to be deleted; must be a positive number.
   */
  void deleteOrder(@NotNull @Min(1) Long id);
}
