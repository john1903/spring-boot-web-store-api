package me.jangluzniewicz.webstore.carts.services;

import java.util.Optional;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class CartServiceFallbackDecorator implements ICart {
  private final ICart primary;
  private final ICart fallback;

  public CartServiceFallbackDecorator(
      @Qualifier("cartServiceWithCache") ICart primary,
      @Qualifier("cartServiceNoCache") ICart fallback) {
    this.primary = primary;
    this.fallback = fallback;
  }

  @Override
  public IdResponse createNewCart(Long customerId) {
    try {
      return primary.createNewCart(customerId);
    } catch (RedisConnectionFailureException | QueryTimeoutException e) {
      return fallback.createNewCart(customerId);
    }
  }

  @Override
  public Optional<Cart> getCartByCustomerId(Long customerId) {
    try {
      return primary.getCartByCustomerId(customerId);
    } catch (RedisConnectionFailureException | QueryTimeoutException e) {
      return fallback.getCartByCustomerId(customerId);
    }
  }

  @Override
  public void updateCart(Long customerId, CartRequest cartRequest) {
    try {
      primary.updateCart(customerId, cartRequest);
    } catch (RedisConnectionFailureException | QueryTimeoutException e) {
      fallback.updateCart(customerId, cartRequest);
    }
  }

  @Override
  public void addProductToCart(Long customerId, CartItemRequest cartItemRequest) {
    try {
      primary.addProductToCart(customerId, cartItemRequest);
    } catch (RedisConnectionFailureException | QueryTimeoutException e) {
      fallback.addProductToCart(customerId, cartItemRequest);
    }
  }

  @Override
  public void emptyCart(Long customerId) {
    try {
      primary.emptyCart(customerId);
    } catch (RedisConnectionFailureException | QueryTimeoutException e) {
      fallback.emptyCart(customerId);
    }
  }
}
