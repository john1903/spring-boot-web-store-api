package me.jangluzniewicz.webstore.users.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Payload for changing a user's password")
@AllArgsConstructor
@Getter
public class ChangeUserPasswordRequest {
  @Schema(description = "Current password of the user", example = "P@ssw0rd")
  @NotNull(message = "currentPassword is required")
  private String currentPassword;

  @Schema(description = "New password of the user", example = "P@ssw0rd")
  @NotNull(message = "newPassword is required")
  @Size(min = 8, max = 255, message = "newPassword must be between 8 and 255 characters")
  private String newPassword;
}
