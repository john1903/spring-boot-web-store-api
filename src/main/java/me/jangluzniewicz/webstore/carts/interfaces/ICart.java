package me.jangluzniewicz.webstore.carts.interfaces;

import java.util.Optional;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.models.Cart;

public interface ICart {
  Long createNewCart(Long customerId);

  Optional<Cart> getCartByCustomerId(Long customerId);

  Long updateCart(Long customerId, CartRequest cartRequest);

  void addProductToCart(Long customerId, CartItemRequest cartItemRequest);

  void emptyCart(Long customerId);
}
