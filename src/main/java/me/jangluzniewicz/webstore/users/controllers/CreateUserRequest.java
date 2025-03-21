package me.jangluzniewicz.webstore.users.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Payload for updating a user")
@AllArgsConstructor
@Getter
public class CreateUserRequest {
  @Schema(
      description = "Identifier of the role assigned to the user",
      example = "2",
      nullable = true)
  @Min(value = 1, message = "roleId must be greater than 0")
  private Long roleId;

  @Schema(description = "Email address of the user", example = "user@example.com")
  @NotNull(message = "email is required")
  @Size(min = 5, max = 255, message = "email must be between 5 and 255 characters")
  @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email format")
  private String email;

  @Schema(description = "Password of the user", example = "P@ssw0rd")
  @NotNull(message = "password is required")
  @Size(min = 8, max = 255, message = "password must be between 8 and 255 characters")
  private String password;

  @Schema(description = "Phone number of the user", example = "+12345678901")
  @NotNull(message = "phoneNumber is required")
  @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phoneNumber format")
  private String phoneNumber;
}
