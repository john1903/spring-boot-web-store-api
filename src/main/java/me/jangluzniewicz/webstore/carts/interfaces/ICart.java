package me.jangluzniewicz.webstore.carts.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.commons.models.IdResponse;

/** Interface for managing shopping carts. */
public interface ICart {

  /**
   * Creates a new cart for a customer.
   *
   * @param customerId the ID of the customer for whom to create the cart; must be a positive
   *     number.
   * @return an {@link IdResponse} containing the ID of the newly created cart.
   */
  IdResponse createNewCart(@NotNull @Min(1) Long customerId);

  /**
   * Retrieves the cart associated with a specific customer ID.
   *
   * @param customerId the ID of the customer whose cart is to be retrieved; must be a positive
   *     number.
   * @return an {@link Optional} containing the {@link Cart} if found, or empty if not found.
   */
  Optional<Cart> getCartByCustomerId(@NotNull @Min(1) Long customerId);

  /**
   * Updates the cart for a specific customer.
   *
   * @param customerId the ID of the customer whose cart is to be updated; must be a positive
   *     number.
   * @param cartRequest the request object containing the updated cart details.
   */
  void updateCart(@NotNull @Min(1) Long customerId, @NotNull CartRequest cartRequest);

  /**
   * Adds a product to the cart for a specific customer.
   *
   * @param customerId the ID of the customer whose cart is to be updated; must be a positive
   *     number.
   * @param cartItemRequest the request object containing the product details to be added to the
   *     cart.
   */
  void addProductToCart(@NotNull @Min(1) Long customerId, @NotNull CartItemRequest cartItemRequest);

  /**
   * Empties the cart for a specific customer.
   *
   * @param customerId the ID of the customer whose cart is to be emptied; must be a positive
   *     number.
   */
  void emptyCart(@NotNull @Min(1) Long customerId);
}
