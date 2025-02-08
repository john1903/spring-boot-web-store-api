package me.jangluzniewicz.webstore.carts.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.entities.CartEntity;
import me.jangluzniewicz.webstore.carts.entities.CartItemEntity;
import me.jangluzniewicz.webstore.carts.mappers.CartMapper;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.repositories.CartRepository;
import me.jangluzniewicz.webstore.carts.services.CartService;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.mappers.ProductMapper;
import me.jangluzniewicz.webstore.products.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
  @Mock private CartRepository cartRepository;
  @Mock private CartMapper cartMapper;
  @Mock private IProduct productService;
  @Mock private ProductMapper productMapper;
  @InjectMocks private CartService cartService;
  private ProductEntity productEntity;
  private Product product;

  @BeforeEach
  void setUp() {
    productEntity =
        ProductEntity.builder()
            .id(1L)
            .name("Motorbike")
            .description("Very fast motorbike")
            .price(BigDecimal.valueOf(1400.0))
            .weight(BigDecimal.valueOf(70.0))
            .category(new CategoryEntity(1L, "Motorbikes"))
            .build();
    product =
        Product.builder()
            .id(1L)
            .name("Motorbike")
            .description("Very fast motorbike")
            .price(BigDecimal.valueOf(1400.0))
            .weight(BigDecimal.valueOf(70.0))
            .category(new Category(1L, "Motorbikes"))
            .build();
  }

  @Test
  public void createNewCart_whenCartDoesNotExist_thenReturnCartId() {
    CartEntity cartEntity =
        CartEntity.builder().id(null).customerId(1L).items(new ArrayList<>()).build();

    when(cartRepository.existsByCustomerId(1L)).thenReturn(false);
    when(cartMapper.toEntity(any())).thenReturn(cartEntity);
    when(cartRepository.save(any()))
        .thenReturn(CartEntity.builder().id(1L).customerId(1L).items(new ArrayList<>()).build());

    Long cartId = cartService.createNewCart(1L);

    assertEquals(1L, cartId);
  }

  @Test
  public void createNewCart_whenCartExists_thenThrowConflictException() {
    when(cartRepository.existsByCustomerId(1L)).thenReturn(true);

    assertThrows(ConflictException.class, () -> cartService.createNewCart(1L));
  }

  @Test
  public void getCartByCustomerId_whenCartExists_thenReturnCart() {
    CartEntity cartEntity =
        CartEntity.builder().id(1L).customerId(1L).items(new ArrayList<>()).build();

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(cartEntity))
        .thenReturn(Cart.builder().id(1L).customerId(1L).items(new ArrayList<>()).build());

    Optional<Cart> cart = cartService.getCartByCustomerId(1L);

    assertTrue(cart.isPresent());
    assertEquals(1L, cart.get().getId());
    assertEquals(1L, cart.get().getCustomerId());
    assertEquals(0, cart.get().getItems().size());
  }

  @Test
  public void getCartByCustomerId_whenCartDoesNotExist_thenReturnEmpty() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    Optional<Cart> cart = cartService.getCartByCustomerId(1L);

    assertTrue(cart.isEmpty());
  }

  @Test
  public void addProductToCart_whenCartAndProductExist_thenAddProduct() {
    CartItemRequest cartItemRequest = new CartItemRequest(null, 1L, 1);

    when(cartRepository.findByCustomerId(1L))
        .thenReturn(
            Optional.of(
                CartEntity.builder().id(1L).customerId(1L).items(new ArrayList<>()).build()));
    when(productService.getProductById(1L)).thenReturn(Optional.of(product));
    when(productMapper.toEntity(product)).thenReturn(productEntity);

    assertDoesNotThrow(() -> cartService.addProductToCart(1L, cartItemRequest));
  }

  @Test
  public void addProductToCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    CartItemRequest cartItemRequest = new CartItemRequest(null, 1L, 1);

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.addProductToCart(1L, cartItemRequest));
  }

  @Test
  public void addProductToCart_whenProductDoesNotExist_thenThrowNotFoundException() {
    CartItemRequest cartItemRequest = new CartItemRequest(null, 1L, 1);

    when(cartRepository.findByCustomerId(1L))
        .thenReturn(
            Optional.of(
                CartEntity.builder().id(1L).customerId(1L).items(new ArrayList<>()).build()));
    when(productService.getProductById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.addProductToCart(1L, cartItemRequest));
  }

  @Test
  public void emptyCart_whenCartExists_thenEmptyCart() {
    CartEntity cartEntity =
        CartEntity.builder()
            .id(1L)
            .customerId(1L)
            .items(
                List.of(CartItemEntity.builder().id(1L).product(productEntity).quantity(1).build()))
            .build();

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntity));

    assertDoesNotThrow(() -> cartService.emptyCart(1L));
  }

  @Test
  public void emptyCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.emptyCart(1L));
  }

  @Test
  public void updateCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    CartRequest cartRequest = new CartRequest(1L, List.of(new CartItemRequest(1L, 1L, 1)));

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.updateCart(1L, cartRequest));
  }

  @Test
  public void updateCart_whenProductDoesNotExist_thenThrowNotFoundException() {
    CartRequest cartRequest = new CartRequest(1L, List.of(new CartItemRequest(null, 1L, 1)));

    when(cartRepository.findByCustomerId(1L))
        .thenReturn(
            Optional.of(
                CartEntity.builder().id(1L).customerId(1L).items(new ArrayList<>()).build()));
    when(productService.getProductById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.updateCart(1L, cartRequest));
  }

  @Test
  public void updateCart_whenCartAndProductExist_thenUpdateCart() {
    CartRequest cartRequest = new CartRequest(1L, List.of(new CartItemRequest(1L, 1L, 3)));

    when(cartRepository.findByCustomerId(1L))
        .thenReturn(
            Optional.of(
                CartEntity.builder()
                    .id(1L)
                    .customerId(1L)
                    .items(List.of(new CartItemEntity(1L, productEntity, 1)))
                    .build()));
    when(productService.getProductById(1L)).thenReturn(Optional.of(product));
    when(productMapper.toEntity(product)).thenReturn(productEntity);

    assertDoesNotThrow(() -> cartService.updateCart(1L, cartRequest));
  }
}
