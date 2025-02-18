package me.jangluzniewicz.webstore.carts.services;

import java.util.Optional;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.mappers.CartMapper;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.repositories.CartRepository;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service("cartServiceWithCache")
@Validated
public class CartServiceWithCache extends AbstractCartService {
  public CartServiceWithCache(
      CartRepository cartRepository, CartMapper cartMapper, IProduct productService) {
    super(cartRepository, cartMapper, productService);
  }

  @Override
  public IdResponse createNewCart(Long customerId) {
    return super.createNewCart(customerId);
  }

  @Override
  @Cacheable(value = "carts", key = "#customerId")
  public Optional<Cart> getCartByCustomerId(Long customerId) {
    return super.getCartByCustomerId(customerId);
  }

  @Override
  @CacheEvict(value = "carts", key = "#customerId")
  public void updateCart(Long customerId, CartRequest cartRequest) {
    super.updateCart(customerId, cartRequest);
  }

  @Override
  @CacheEvict(value = "carts", key = "#customerId")
  public void addProductToCart(Long customerId, CartItemRequest cartItemRequest) {
    super.addProductToCart(customerId, cartItemRequest);
  }

  @Override
  @CacheEvict(value = "carts", key = "#customerId")
  public void emptyCart(Long customerId) {
    super.emptyCart(customerId);
  }
}
