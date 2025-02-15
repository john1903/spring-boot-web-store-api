package me.jangluzniewicz.webstore.carts.controllers;

import jakarta.validation.Valid;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.security.models.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping("/carts")
public class CartController {
  private final ICart cartService;

  public CartController(ICart cartService) {
    this.cartService = cartService;
  }

  @GetMapping("/current")
  public ResponseEntity<Cart> getCart() {
    CustomUser principal =
        (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return cartService
        .getCartByCustomerId(principal.getId())
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/current")
  public ResponseEntity<Void> updateCart(@Valid @RequestBody CartRequest cartRequest) {
    CustomUser principal =
        (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    cartService.updateCart(principal.getId(), cartRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/current/items")
  public ResponseEntity<Void> addItemToCart(@Valid @RequestBody CartItemRequest cartItemRequest) {
    CustomUser principal =
        (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    cartService.addProductToCart(principal.getId(), cartItemRequest);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/current/items")
  public ResponseEntity<Void> emptyCart() {
    CustomUser principal =
        (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    cartService.emptyCart(principal.getId());
    return ResponseEntity.noContent().build();
  }
}
