package me.jangluzniewicz.webstore.security.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Void> createUser(@Valid @RequestBody UserRequest userRequest) {
    Long userId = userService.registerNewUser(userRequest);
    return ResponseEntity.created(URI.create("/users/" + userId)).build();
  }
}
