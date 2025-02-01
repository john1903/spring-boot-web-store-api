package me.jangluzniewicz.webstore.carts.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.jangluzniewicz.webstore.carts.controllers.CartItemRequest;
import me.jangluzniewicz.webstore.carts.controllers.CartRequest;
import me.jangluzniewicz.webstore.carts.entities.CartEntity;
import me.jangluzniewicz.webstore.carts.entities.CartItemEntity;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.carts.mappers.CartMapper;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.carts.repositories.CartRepository;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.mappers.ProductMapper;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.users.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService implements ICart {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final IProduct productService;
    private final ProductMapper productMapper;

    public CartService(CartRepository cartRepository, CartMapper cartMapper,
                       IProduct productService, ProductMapper productMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public Long createNewCart(@NotNull User customer) {
        Cart cart = Cart.builder()
                .customer(customer)
                .items(new ArrayList<>())
                .build();
        return cartRepository.save(cartMapper.toEntity(cart)).getId();
    }

    @Override
    public Optional<Cart> getCartByCustomerId(@NotNull @Min(1) Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .map(cartMapper::fromEntity);
    }

    @Override
    @Transactional
    public Long updateCart(@NotNull @Min(1) Long customerId, @NotNull CartRequest cartRequest) {
        CartEntity cartEntity = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Cart for customer with id " + customerId + " not found"));
        List<CartItemEntity> cartItems = cartRequest.getItems().stream().map(cartItemRequest -> {
            Product product = productService.getProductById(cartItemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product with id "
                            + cartItemRequest.getProductId() + " not found"));
            return CartItemEntity.builder()
                    .id(cartItemRequest.getId())
                    .product(productMapper.toEntity(product))
                    .quantity(cartItemRequest.getQuantity())
                    .build();
        }).toList();
        cartEntity.setItems(cartItems);
        return cartEntity.getId();
    }

    @Override
    @Transactional
    public void addProductToCart(@NotNull @Min(1) Long customerId, @NotNull CartItemRequest cartItemRequest) {
        CartEntity cartEntity = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Cart for customer with id " + customerId + " not found"));
        Product product = productService.getProductById(cartItemRequest.getProductId())
                .orElseThrow(() -> new NotFoundException("Product with id "
                        + cartItemRequest.getProductId() + " not found"));
        CartItemEntity cartItemEntity = CartItemEntity.builder()
                .product(productMapper.toEntity(product))
                .quantity(cartItemRequest.getQuantity())
                .build();
        cartEntity.getItems().add(cartItemEntity);
    }

    @Override
    @Transactional
    public void emptyCart(@NotNull @Min(1) Long customerId) {
        CartEntity cartEntity = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Cart for customer with id " + customerId + " not found"));
        cartEntity.setItems(new ArrayList<>());
    }
}
