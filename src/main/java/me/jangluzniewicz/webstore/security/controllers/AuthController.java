package me.jangluzniewicz.webstore.security.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
  private final IUser userService;

  public AuthController(IUser userService) {
    this.userService = userService;
  }

  @PreAuthorize("#userRequest.roleId != 1 or hasRole('ADMIN')")
  @PostMapping("/signup")
  public ResponseEntity<IdResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
    IdResponse response = userService.registerNewUser(userRequest);
    return ResponseEntity.created(URI.create("/users/" + response.getId())).body(response);
  }
}
