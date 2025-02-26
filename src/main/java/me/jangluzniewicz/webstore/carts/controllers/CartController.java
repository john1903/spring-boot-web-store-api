package me.jangluzniewicz.webstore.carts.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.jangluzniewicz.webstore.carts.interfaces.ICart;
import me.jangluzniewicz.webstore.carts.models.Cart;
import me.jangluzniewicz.webstore.security.interfaces.ISecurity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carts", description = "Operations related to customer carts")
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

  @Operation(
      summary = "Get current cart",
      description = "Retrieves the current cart for the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "200",
      description = "Cart found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Cart.class)))
  @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content)
  @GetMapping("/current")
  public ResponseEntity<Cart> getCart() {
    return cartService
        .getCartByCustomerId(authService.getCurrentUser().getId())
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(
      summary = "Update current cart",
      description = "Updates the current cart for the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Cart updated", content = @Content)
  @PutMapping("/current")
  public ResponseEntity<Void> updateCart(
      @RequestBody(
              description = "Cart update payload",
              required = true,
              content = @Content(schema = @Schema(implementation = CartRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          CartRequest cartRequest) {
    cartService.updateCart(authService.getCurrentUser().getId(), cartRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Add item to current cart",
      description = "Adds a new item to the current cart of the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Item added", content = @Content)
  @PostMapping("/current/items")
  public ResponseEntity<Void> addItemToCart(
      @RequestBody(
              description = "Cart item payload",
              required = true,
              content = @Content(schema = @Schema(implementation = CartItemRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          CartItemRequest cartItemRequest) {
    cartService.addProductToCart(authService.getCurrentUser().getId(), cartItemRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Empty current cart",
      description = "Empties the cart for the authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Cart emptied", content = @Content)
  @DeleteMapping("/current/items")
  public ResponseEntity<Void> emptyCart() {
    cartService.emptyCart(authService.getCurrentUser().getId());
    return ResponseEntity.noContent().build();
  }
}
