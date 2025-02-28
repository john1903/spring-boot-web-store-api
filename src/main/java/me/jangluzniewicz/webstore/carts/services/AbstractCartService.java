package me.jangluzniewicz.webstore.carts.services;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.carts.mappers.CartMapper;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.models.CartItem;
import me.jangluzniewicz.webstore.carts.repositories.CartRepository;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;

public abstract class AbstractCartService implements ICart {
  protected final CartRepository cartRepository;
  protected final CartMapper cartMapper;
  protected final IProduct productService;

  public AbstractCartService(
      CartRepository cartRepository, CartMapper cartMapper, IProduct productService) {
    this.cartRepository = cartRepository;
    this.cartMapper = cartMapper;
    this.productService = productService;
  }

  @Override
  @Transactional
  public IdResponse createNewCart(Long customerId) {
    if (cartRepository.existsByCustomerId(customerId)) {
      throw new ConflictException("Cart for customer with id " + customerId + " already exists");
    }
    Cart cart = Cart.builder().customerId(customerId).items(new ArrayList<>()).build();
    return new IdResponse(cartRepository.save(cartMapper.toEntity(cart)).getId());
  }

  @Override
  public Optional<Cart> getCartByCustomerId(Long customerId) {
    return cartRepository.findByCustomerId(customerId).map(cartMapper::fromEntity);
  }

  @Override
  @Transactional
  public void updateCart(Long customerId, CartRequest cartRequest) {
    Cart cart = fetchCart(customerId);
    cart.setItems(
        cartRequest.getItems().stream()
            .map(
                cartItemRequest ->
                    CartItem.builder()
                        .id(cartItemRequest.getId())
                        .product(
                            productService
                                .getProductById(cartItemRequest.getProductId())
                                .orElseThrow(
                                    () ->
                                        new NotFoundException(
                                            "Product with id "
                                                + cartItemRequest.getProductId()
                                                + " not found")))
                        .quantity(cartItemRequest.getQuantity())
                        .build())
            .toList());
    cartRepository.save(cartMapper.toEntity(cart));
  }

  @Override
  @Transactional
  public void addProductToCart(Long customerId, CartItemRequest cartItemRequest) {
    Cart cart = fetchCart(customerId);
    Optional<CartItem> existingItem =
        cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(cartItemRequest.getProductId()))
            .findFirst();
    if (existingItem.isPresent()) {
      existingItem
          .get()
          .setQuantity(existingItem.get().getQuantity() + cartItemRequest.getQuantity());
    } else {
      cart.getItems()
          .add(
              CartItem.builder()
                  .product(
                      productService
                          .getProductById(cartItemRequest.getProductId())
                          .orElseThrow(
                              () ->
                                  new NotFoundException(
                                      "Product with id "
                                          + cartItemRequest.getProductId()
                                          + " not found")))
                  .quantity(cartItemRequest.getQuantity())
                  .build());
    }
    cartRepository.save(cartMapper.toEntity(cart));
  }

  @Override
  @Transactional
  public void emptyCart(Long customerId) {
    Cart cart = fetchCart(customerId);
    cart.setItems(new ArrayList<>());
    cartRepository.save(cartMapper.toEntity(cart));
  }

  protected Cart fetchCart(Long customerId) {
    return cartRepository
        .findByCustomerId(customerId)
        .map(cartMapper::fromEntity)
        .orElseThrow(
            () -> new NotFoundException("Cart for customer with id " + customerId + " not found"));
  }
}
