package me.jangluzniewicz.webstore.users.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.users.interfaces.IUser;
import me.jangluzniewicz.webstore.users.models.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Operations related to users")
@RestController
@RequestMapping("/users")
public class UserController {
  private final IUser userService;

  public UserController(IUser userService) {
    this.userService = userService;
  }

  @Operation(
      summary = "Get users",
      description = "Returns a paginated list of users (ADMIN only)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "200",
      description = "List of users",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PagedResponse.class)))
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<PagedResponse<User>> getUsers(
      @Parameter(in = ParameterIn.QUERY, description = "Page number", example = "0")
          @RequestParam(defaultValue = "0")
          Integer page,
      @Parameter(in = ParameterIn.QUERY, description = "Page size", example = "20")
          @RequestParam(defaultValue = "20")
          Integer size) {
    return ResponseEntity.ok(userService.getAllUsers(page, size));
  }

  @Operation(
      summary = "Get user by ID",
      description =
          "Returns a user based on the provided ID. Accessible for ADMIN or the user itself.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "200",
      description = "User found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(
      @Parameter(in = ParameterIn.PATH, description = "User ID", required = true, example = "1")
          @PathVariable
          Long id) {
    return userService
        .getUserById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new NotFoundException("User not found"));
  }

  @Operation(
      summary = "Update user",
      description =
          "Updates an existing user. Accessible for ADMIN or the user itself (if not setting roleId to 1)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "User updated", content = @Content)
  @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  @ApiResponse(responseCode = "409", description = "User email already exists", content = @Content)
  @PreAuthorize(
      "hasRole('ADMIN') or (#id == authentication.principal.id and #updateUserRequest.roleId != 1)")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateUser(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the user to update",
              required = true,
              example = "1")
          @PathVariable
          Long id,
      @RequestBody(
              description = "User update payload",
              required = true,
              content = @Content(schema = @Schema(implementation = UpdateUserRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          UpdateUserRequest updateUserRequest) {
    userService.updateUser(id, updateUserRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Change user password",
      description = "Changes the password of the user. Accessible for ADMIN or the user itself.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Password changed", content = @Content)
  @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
  @PutMapping("/{id}/password")
  public ResponseEntity<Void> changePassword(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the user to update",
              required = true,
              example = "1")
          @PathVariable
          Long id,
      @RequestBody(
              description = "User password change payload",
              required = true,
              content =
                  @Content(schema = @Schema(implementation = ChangeUserPasswordRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          ChangeUserPasswordRequest changeUserPasswordRequest) {
    userService.changePassword(id, changeUserPasswordRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Delete user",
      description = "Deletes a user by its ID (ADMIN only)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "User deleted", content = @Content)
  @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  @ApiResponse(responseCode = "409", description = "User has active orders", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the user to delete",
              required = true,
              example = "1")
          @PathVariable
          Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
