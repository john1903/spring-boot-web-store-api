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
import me.jangluzniewicz.webstore.carts.entities.CartItemEntity;
import me.jangluzniewicz.webstore.carts.mappers.CartMapper;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.repositories.CartRepository;
import me.jangluzniewicz.webstore.carts.services.CartService;
import me.jangluzniewicz.webstore.exceptions.ConflictException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
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
  private CartEntity cartEmptyEntity;
  private Cart cartEmpty;
  private Product product;
  private ProductEntity productEntity;
  private CartItemRequest addProductRequest;
  private CartRequest defaultCartRequest;

  @BeforeEach
  void setUp() {
    addProductRequest = new CartItemRequest(null, 1L, 1);
    defaultCartRequest = new CartRequest(1L, List.of(new CartItemRequest(1L, 1L, 3)));
  }

  @Test
  void createNewCart_whenCartDoesNotExist_thenReturnCartId() {
    when(cartRepository.existsByCustomerId(1L)).thenReturn(false);
    when(cartRepository.save(any())).thenReturn(cartEmptyEntity);

    assertEquals(1L, cartService.createNewCart(1L));
  }

  @Test
  void createNewCart_whenCartExists_thenThrowConflictException() {
    when(cartRepository.existsByCustomerId(1L)).thenReturn(true);

    assertThrows(ConflictException.class, () -> cartService.createNewCart(1L));
  }

  @Test
  void getCartByCustomerId_whenCartExists_thenReturnCart() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEmptyEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cartEmpty);

    assertTrue(cartService.getCartByCustomerId(1L).isPresent());
  }

  @Test
  void getCartByCustomerId_whenCartDoesNotExist_thenReturnEmpty() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertTrue(cartService.getCartByCustomerId(1L).isEmpty());
  }

  @Test
  void addProductToCart_whenCartAndProductExist_thenAddProduct() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEmptyEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cartEmpty);
    when(productService.getProductById(1L)).thenReturn(Optional.of(product));
    when(cartRepository.save(any())).thenReturn(cartEntity);

    assertDoesNotThrow(() -> cartService.addProductToCart(1L, addProductRequest));
  }

  @Test
  void addProductToCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> cartService.addProductToCart(1L, addProductRequest));
  }

  @Test
  void addProductToCart_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEmptyEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cartEmpty);
    when(productService.getProductById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> cartService.addProductToCart(1L, addProductRequest));
  }

  @Test
  void cart_whenCartExists_thencart() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cart);
    when(cartRepository.save(any())).thenReturn(cartEmptyEntity);

    assertDoesNotThrow(() -> cartService.emptyCart(1L));
  }

  @Test
  void cart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.emptyCart(1L));
  }

  @Test
  void updateCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.updateCart(1L, defaultCartRequest));
  }

  @Test
  void updateCart_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEmptyEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cartEmpty);
    when(productService.getProductById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.updateCart(1L, defaultCartRequest));
  }

  @Test
  void updateCart_whenCartAndProductExist_thenUpdateCart() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cart);
    when(productService.getProductById(1L)).thenReturn(Optional.of(product));
    CartEntity updatedEntity =
        CartEntity.builder()
            .id(1L)
            .customerId(1L)
            .items(
                List.of(CartItemEntity.builder().id(1L).product(productEntity).quantity(3).build()))
            .build();
    when(cartRepository.save(any())).thenReturn(updatedEntity);

    assertDoesNotThrow(() -> cartService.updateCart(1L, defaultCartRequest));
  }
}
