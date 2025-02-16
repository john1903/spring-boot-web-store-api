package me.jangluzniewicz.webstore.carts.controllers;

import jakarta.validation.Valid;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.security.interfaces.ISecurity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/carts")
public class CartController {
  private final ICart cartService;
  private final ISecurity authService;

  public CartController(ICart cartService, ISecurity authService) {
    this.cartService = cartService;
    this.authService = authService;
  }

  @GetMapping("/current")
  public ResponseEntity<Cart> getCart() {
    return cartService
        .getCartByCustomerId(authService.getCurrentUser().getId())
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/current")
  public ResponseEntity<Void> updateCart(@Valid @RequestBody CartRequest cartRequest) {
    cartService.updateCart(authService.getCurrentUser().getId(), cartRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/current/items")
  public ResponseEntity<Void> addItemToCart(@Valid @RequestBody CartItemRequest cartItemRequest) {
    cartService.addProductToCart(authService.getCurrentUser().getId(), cartItemRequest);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/current/items")
  public ResponseEntity<Void> emptyCart() {
    cartService.emptyCart(authService.getCurrentUser().getId());
    return ResponseEntity.noContent().build();
  }
}
