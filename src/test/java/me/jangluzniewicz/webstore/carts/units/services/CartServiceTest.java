package me.jangluzniewicz.webstore.carts.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import me.jangluzniewicz.webstore.carts.models.CartItem;
import me.jangluzniewicz.webstore.carts.repositories.CartRepository;
import me.jangluzniewicz.webstore.carts.services.CartService;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.models.Category;
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

  private ProductEntity productEntity;
  private Product product;

  private CartEntity emptyCartEntity;
  private Cart emptyCart;
  private CartEntity cartEntityWithItem;
  private Cart cartWithItem;

  private CartItemRequest addProductRequest;
  private CartRequest defaultCartRequest;

  @BeforeEach
  void setUp() {
    productEntity =
        ProductEntity.builder()
            .id(1L)
            .name("Motorbike")
            .description("Very fast motorbike")
            .price(BigDecimal.valueOf(1400.0))
            .weight(BigDecimal.valueOf(70.0))
            .category(CategoryEntity.builder().id(1L).name("Motorbikes").build())
            .build();
    product =
        Product.builder()
            .id(1L)
            .name("Motorbike")
            .description("Very fast motorbike")
            .price(BigDecimal.valueOf(1400.0))
            .weight(BigDecimal.valueOf(70.0))
            .category(Category.builder().id(1L).name("Motorbikes").build())
            .build();

    emptyCartEntity = CartEntity.builder().id(1L).customerId(1L).items(new ArrayList<>()).build();
    emptyCart = Cart.builder().id(1L).customerId(1L).items(new ArrayList<>()).build();

    cartEntityWithItem =
        CartEntity.builder()
            .id(1L)
            .customerId(1L)
            .items(
                List.of(CartItemEntity.builder().id(1L).product(productEntity).quantity(1).build()))
            .build();
    cartWithItem =
        Cart.builder()
            .id(1L)
            .customerId(1L)
            .items(List.of(CartItem.builder().id(1L).product(product).quantity(1).build()))
            .build();

    addProductRequest = new CartItemRequest(null, 1L, 1);
    defaultCartRequest = new CartRequest(1L, List.of(new CartItemRequest(1L, 1L, 3)));
  }

  @Test
  void createNewCart_whenCartDoesNotExist_thenReturnCartId() {
    when(cartRepository.existsByCustomerId(1L)).thenReturn(false);
    when(cartRepository.save(any())).thenReturn(emptyCartEntity);

    assertEquals(1L, cartService.createNewCart(1L));
  }

  @Test
  void createNewCart_whenCartExists_thenThrowConflictException() {
    when(cartRepository.existsByCustomerId(1L)).thenReturn(true);

    assertThrows(ConflictException.class, () -> cartService.createNewCart(1L));
  }

  @Test
  void getCartByCustomerId_whenCartExists_thenReturnCart() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(emptyCartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(emptyCart);

    assertTrue(cartService.getCartByCustomerId(1L).isPresent());
  }

  @Test
  void getCartByCustomerId_whenCartDoesNotExist_thenReturnEmpty() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertTrue(cartService.getCartByCustomerId(1L).isEmpty());
  }

  @Test
  void addProductToCart_whenCartAndProductExist_thenAddProduct() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(emptyCartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(emptyCart);
    when(productService.getProductById(1L)).thenReturn(Optional.of(product));
    when(cartRepository.save(any())).thenReturn(cartEntityWithItem);

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
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(emptyCartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(emptyCart);
    when(productService.getProductById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> cartService.addProductToCart(1L, addProductRequest));
  }

  @Test
  void emptyCart_whenCartExists_thenEmptyCart() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntityWithItem));
    when(cartMapper.fromEntity(any())).thenReturn(cartWithItem);
    when(cartRepository.save(any())).thenReturn(emptyCartEntity);

    assertDoesNotThrow(() -> cartService.emptyCart(1L));
  }

  @Test
  void emptyCart_whenCartDoesNotExist_thenThrowNotFoundException() {
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
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(emptyCartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(emptyCart);
    when(productService.getProductById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.updateCart(1L, defaultCartRequest));
  }

  @Test
  void updateCart_whenCartAndProductExist_thenUpdateCart() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntityWithItem));
    when(cartMapper.fromEntity(any())).thenReturn(cartWithItem);
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
