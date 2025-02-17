package me.jangluzniewicz.webstore.carts.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.common.models.IdResponse;

public interface ICart {
  IdResponse createNewCart(@NotNull @Min(1) Long customerId);

  Optional<Cart> getCartByCustomerId(@NotNull @Min(1) Long customerId);

  void updateCart(@NotNull @Min(1) Long customerId, @NotNull CartRequest cartRequest);

  void addProductToCart(@NotNull @Min(1) Long customerId, @NotNull CartItemRequest cartItemRequest);

  void emptyCart(@NotNull @Min(1) Long customerId);
}
