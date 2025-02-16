package me.jangluzniewicz.webstore.carts.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Optional;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.carts.mappers.CartMapper;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.models.CartItem;
import me.jangluzniewicz.webstore.carts.repositories.CartRepository;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import org.springframework.stereotype.Service;

@Service
public class CartService implements ICart {
  private final CartRepository cartRepository;
  private final CartMapper cartMapper;
  private final IProduct productService;

  public CartService(
      CartRepository cartRepository, CartMapper cartMapper, IProduct productService) {
    this.cartRepository = cartRepository;
    this.cartMapper = cartMapper;
    this.productService = productService;
  }

  @Override
  @Transactional
  public IdResponse createNewCart(@NotNull Long customerId) {
    if (cartRepository.existsByCustomerId(customerId)) {
      throw new ConflictException("Cart for customer with id " + customerId + " already exists");
    }
    Cart cart = Cart.builder().customerId(customerId).items(new ArrayList<>()).build();
    return new IdResponse(cartRepository.save(cartMapper.toEntity(cart)).getId());
  }

  @Override
  public Optional<Cart> getCartByCustomerId(@NotNull @Min(1) Long customerId) {
    return cartRepository.findByCustomerId(customerId).map(cartMapper::fromEntity);
  }

  @Override
  @Transactional
  public void updateCart(@NotNull @Min(1) Long customerId, @NotNull CartRequest cartRequest) {
    Cart cart =
        getCartByCustomerId(customerId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Cart for customer with id " + customerId + " not found"));
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
  public void addProductToCart(
      @NotNull @Min(1) Long customerId, @NotNull CartItemRequest cartItemRequest) {
    Cart cart =
        getCartByCustomerId(customerId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Cart for customer with id " + customerId + " not found"));
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
    cartRepository.save(cartMapper.toEntity(cart));
  }

  @Override
  @Transactional
  public void emptyCart(@NotNull @Min(1) Long customerId) {
    Cart cart =
        getCartByCustomerId(customerId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Cart for customer with id " + customerId + " not found"));
    cart.setItems(new ArrayList<>());
    cartRepository.save(cartMapper.toEntity(cart));
  }
}
