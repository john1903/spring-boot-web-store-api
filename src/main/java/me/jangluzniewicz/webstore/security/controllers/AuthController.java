package me.jangluzniewicz.webstore.security.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import me.jangluzniewicz.webstore.security.models.CustomUser;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
  private final IUser userService;

  public AuthController(IUser userService) {
    this.userService = userService;
  }

  @PostMapping("/signup")
  public ResponseEntity<Void> createUser(@Valid @RequestBody UserRequest userRequest)
      throws AccessDeniedException {
    final long ADMIN_ROLE_ID = 1L;
    CustomUser principal =
        (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (userRequest.getRoleId() == ADMIN_ROLE_ID
        && (principal == null || !principal.hasRole("ADMIN"))) {
      throw new AccessDeniedException("Cannot create user with ADMIN role");
    }
    Long userId = userService.registerNewUser(userRequest);
    return ResponseEntity.created(URI.create("/users/" + userId)).build();
  }
}
