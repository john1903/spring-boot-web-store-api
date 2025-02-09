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

  @BeforeEach
  void setUp() {
    CategoryEntity categoryEntity = new CategoryEntity(1L, "Motorbikes");
    Category category = new Category(1L, "Motorbikes");

    productEntity =
        ProductEntity.builder()
            .id(1L)
            .name("Motorbike")
            .description("Very fast motorbike")
            .price(BigDecimal.valueOf(1400.0))
            .weight(BigDecimal.valueOf(70.0))
            .category(categoryEntity)
            .build();

    product =
        Product.builder()
            .id(1L)
            .name("Motorbike")
            .description("Very fast motorbike")
            .price(BigDecimal.valueOf(1400.0))
            .weight(BigDecimal.valueOf(70.0))
            .category(category)
            .build();
  }

  private CartEntity createCartEntity(Long id, List<CartItemEntity> items) {
    return CartEntity.builder().id(id).customerId(1L).items(items).build();
  }

  private Cart createCart(Long id, List<CartItem> items) {
    return Cart.builder().id(id).customerId(1L).items(items).build();
  }

  @Test
  public void createNewCart_whenCartDoesNotExist_thenReturnCartId() {
    when(cartRepository.existsByCustomerId(1L)).thenReturn(false);
    when(cartRepository.save(any())).thenReturn(createCartEntity(1L, new ArrayList<>()));

    assertEquals(1L, cartService.createNewCart(1L));
  }

  @Test
  public void createNewCart_whenCartExists_thenThrowConflictException() {
    when(cartRepository.existsByCustomerId(1L)).thenReturn(true);

    assertThrows(ConflictException.class, () -> cartService.createNewCart(1L));
  }

  @Test
  public void getCartByCustomerId_whenCartExists_thenReturnCart() {
    CartEntity cartEntity = createCartEntity(1L, new ArrayList<>());
    Cart cart = createCart(1L, new ArrayList<>());

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cart);

    assertTrue(cartService.getCartByCustomerId(1L).isPresent());
  }

  @Test
  public void getCartByCustomerId_whenCartDoesNotExist_thenReturnEmpty() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    Optional<Cart> cart = cartService.getCartByCustomerId(1L);

    assertTrue(cart.isEmpty());
  }

  @Test
  public void addProductToCart_whenCartAndProductExist_thenAddProduct() {
    CartEntity cartEntity = createCartEntity(1L, new ArrayList<>());
    Cart cart = createCart(1L, new ArrayList<>());

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cart);
    when(productService.getProductById(1L)).thenReturn(Optional.of(product));

    CartItemEntity item =
        CartItemEntity.builder().id(1L).product(productEntity).quantity(1).build();

    when(cartRepository.save(any())).thenReturn(createCartEntity(1L, List.of(item)));

    assertDoesNotThrow(() -> cartService.addProductToCart(1L, new CartItemRequest(null, 1L, 1)));
  }

  @Test
  public void addProductToCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> cartService.addProductToCart(1L, new CartItemRequest(null, 1L, 1)));
  }

  @Test
  public void addProductToCart_whenProductDoesNotExist_thenThrowNotFoundException() {
    CartEntity cartEntity = createCartEntity(1L, new ArrayList<>());
    Cart cart = createCart(1L, new ArrayList<>());

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cart);
    when(productService.getProductById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> cartService.addProductToCart(1L, new CartItemRequest(null, 1L, 1)));
  }

  @Test
  public void emptyCart_whenCartExists_thenEmptyCart() {
    CartItemEntity item =
        CartItemEntity.builder().id(1L).product(productEntity).quantity(1).build();
    CartEntity cartEntityWithItem = createCartEntity(1L, List.of(item));
    CartItem cartItem = CartItem.builder().id(1L).product(product).quantity(1).build();
    Cart cartWithItem = createCart(1L, List.of(cartItem));
    CartEntity emptyCartEntity = createCartEntity(1L, new ArrayList<>());

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntityWithItem));
    when(cartMapper.fromEntity(any())).thenReturn(cartWithItem);
    when(cartRepository.save(any())).thenReturn(emptyCartEntity);

    assertDoesNotThrow(() -> cartService.emptyCart(1L));
  }

  @Test
  public void emptyCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cartService.emptyCart(1L));
  }

  @Test
  public void updateCart_whenCartDoesNotExist_thenThrowNotFoundException() {
    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

    CartRequest cartRequest = new CartRequest(1L, List.of(new CartItemRequest(1L, 1L, 1)));

    assertThrows(NotFoundException.class, () -> cartService.updateCart(1L, cartRequest));
  }

  @Test
  public void updateCart_whenProductDoesNotExist_thenThrowNotFoundException() {
    CartEntity cartEntity = createCartEntity(1L, new ArrayList<>());
    Cart cart = createCart(1L, new ArrayList<>());

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntity));
    when(cartMapper.fromEntity(any())).thenReturn(cart);
    when(productService.getProductById(1L)).thenReturn(Optional.empty());

    CartRequest cartRequest = new CartRequest(1L, List.of(new CartItemRequest(null, 1L, 1)));

    assertThrows(NotFoundException.class, () -> cartService.updateCart(1L, cartRequest));
  }

  @Test
  public void updateCart_whenCartAndProductExist_thenUpdateCart() {
    CartItemEntity item =
        CartItemEntity.builder().id(1L).product(productEntity).quantity(1).build();
    CartEntity cartEntityWithItem = createCartEntity(1L, List.of(item));
    CartItem cartItem = CartItem.builder().id(1L).product(product).quantity(1).build();
    Cart cartWithItem = createCart(1L, List.of(cartItem));

    when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cartEntityWithItem));
    when(cartMapper.fromEntity(any())).thenReturn(cartWithItem);
    when(productService.getProductById(1L)).thenReturn(Optional.of(product));

    CartItemEntity updatedItem =
        CartItemEntity.builder().id(1L).product(productEntity).quantity(3).build();

    when(cartRepository.save(any())).thenReturn(createCartEntity(1L, List.of(updatedItem)));

    CartRequest cartRequest = new CartRequest(1L, List.of(new CartItemRequest(1L, 1L, 3)));

    assertDoesNotThrow(() -> cartService.updateCart(1L, cartRequest));
  }
}
