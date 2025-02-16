package me.jangluzniewicz.webstore.carts.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.entities.CartEntity;
import me.jangluzniewicz.webstore.carts.mappers.CartMapper;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.repositories.CartRepository;
import me.jangluzniewicz.webstore.carts.services.CartService;
import me.jangluzniewicz.webstore.common.testdata.carts.*;
import me.jangluzniewicz.webstore.common.testdata.products.ProductTestDataBuilder;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
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
  @InjectMocks private CartService cartService;

  private CartEntity cartEntity;
  private Cart cart;
  private Product product;
  private CartItemRequest cartItemRequest;
  private CartRequest cartRequest;
  private final Long CUSTOMER_ID = 1L;

  @BeforeEach
  void setUp() {
    product = ProductTestDataBuilder.builder().build().buildProduct();
    cartEntity = CartEntityTestDataBuilder.builder().build().buildCartEntity();
    cart = CartTestDataBuilder.builder().build().buildCart();
    cartRequest =
        CartRequestTestDataBuilder.builder()
            .items(List.of(CartItemRequestTestDataBuilder.builder().build()))
            .build()
            .buildCartRequest();
    cartItemRequest = CartItemRequestTestDataBuilder.builder().build().buildCartItemRequest();
  }

  @Test
  void createNewCart_whenCartDoesNotExist_thenReturnIdResponse() {
    when(cartRepository.existsByCustomerId(CUSTOMER_ID)).thenReturn(false);
    when(cartRepository.save(any())).thenReturn(cartEntity);

    assertEquals(cartEntity.getId(), cartService.createNewCart(CUSTOMER_ID).getId());
  }

  @Test
  void createNewCart_whenCartExists_thenThrowConflictException() {
    when(cartRepository.existsByCustomerId(CUSTOMER_ID)).thenReturn(true);

    assertThrows(ConflictException.class, () -> cartService.createNewCart(CUSTOMER_ID));
  }

  @Test
  void getCartByCustomerId_whenCartExists_thenReturnCart() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(cartEntity)).thenReturn(cart);

    assertTrue(cartService.getCartByCustomerId(CUSTOMER_ID).isPresent());
  }

  @Test
  void getCartByCustomerId_whenCartDoesNotExist_thenReturnEmpty() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.empty());

    assertTrue(cartService.getCartByCustomerId(CUSTOMER_ID).isEmpty());
  }

  @Test
  void addProductToCart_whenCartAndProductExist_thenAddProduct() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(cartEntity)).thenReturn(cart);
    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
    CartEntity cartWithItemEntity =
        CartEntityTestDataBuilder.builder()
            .items(List.of(CartItemEntityTestDataBuilder.builder().build()))
            .build()
            .buildCartEntity();
    when(cartRepository.save(any())).thenReturn(cartWithItemEntity);

    assertDoesNotThrow(() -> cartService.addProductToCart(CUSTOMER_ID, cartItemRequest));
  }

  @Test
  void addProductToCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> cartService.addProductToCart(CUSTOMER_ID, cartItemRequest));
  }

  @Test
  void addProductToCart_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(cartEntity)).thenReturn(cart);
    when(productService.getProductById(product.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> cartService.addProductToCart(CUSTOMER_ID, cartItemRequest));
  }

  @Test
  void cart_whenCartExists_thenEmptyCart() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(cartEntity)).thenReturn(cart);
    when(cartRepository.save(any())).thenReturn(cartEntity);

    assertDoesNotThrow(() -> cartService.emptyCart(CUSTOMER_ID));
  }

  @Test
  void cart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.emptyCart(CUSTOMER_ID));
  }

  @Test
  void updateCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.updateCart(CUSTOMER_ID, cartRequest));
  }

  @Test
  void updateCart_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(cartEntity)).thenReturn(cart);
    when(productService.getProductById(product.getId())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.updateCart(CUSTOMER_ID, cartRequest));
  }

  @Test
  void updateCart_whenCartAndProductExist_thenUpdateCart() {
    when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cart);
    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
    CartEntity updatedEntity =
        CartEntityTestDataBuilder.builder()
            .items(List.of(CartItemEntityTestDataBuilder.builder().build()))
            .build()
            .buildCartEntity();
    when(cartRepository.save(any())).thenReturn(updatedEntity);

    assertDoesNotThrow(() -> cartService.updateCart(CUSTOMER_ID, cartRequest));
  }
}
