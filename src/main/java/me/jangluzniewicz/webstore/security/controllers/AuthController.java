package me.jangluzniewicz.webstore.security.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.users.controllers.UserRequest;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Authentication",
    description = "Operations related to user authentication and registration")
@RestController
@RequestMapping("/auth")
public class AuthController {
  private final IUser userService;

  public AuthController(IUser userService) {
    this.userService = userService;
  }

  @Operation(
      summary = "User Signup",
      description =
          "Registers a new user. If the roleId is not 1, any user can sign up; otherwise ADMIN role is required.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "201",
      description = "User registered successfully",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = IdResponse.class)))
  @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  @ApiResponse(responseCode = "409", description = "Email already in use", content = @Content)
  @PreAuthorize("#userRequest.roleId != 1 or hasRole('ADMIN')")
  @PostMapping("/signup")
  public ResponseEntity<IdResponse> createUser(
      @RequestBody(
              description = "User registration payload",
              required = true,
              content = @Content(schema = @Schema(implementation = UserRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          UserRequest userRequest) {
    IdResponse response = userService.registerNewUser(userRequest);
    return ResponseEntity.created(URI.create("/users/" + response.getId())).body(response);
  }
}
