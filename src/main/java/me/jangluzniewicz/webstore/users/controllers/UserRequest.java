package me.jangluzniewicz.webstore.users.controllers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRequest {
  @NotNull(message = "roleId is required")
  private Long roleId;

  @NotNull(message = "email is required")
  @Size(min = 5, max = 255, message = "email must be between 5 and 255 characters")
  @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email format")
  private String email;

  @NotNull(message = "password is required")
  private String password;

  @NotNull(message = "phoneNumber is required")
  @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phoneNumber format")
  private String phoneNumber;
}
