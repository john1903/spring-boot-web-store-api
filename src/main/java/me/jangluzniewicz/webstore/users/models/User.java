package me.jangluzniewicz.webstore.users.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.jangluzniewicz.webstore.roles.models.Role;

@Schema(description = "Model representing a user")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class User {
  @Schema(description = "Unique identifier of the user", example = "1")
  private Long id;

  @Schema(description = "Role assigned to the user")
  private Role role;

  @NonNull
  @Schema(description = "Email address of the user", example = "user@example.com")
  private String email;

  @NonNull
  @Schema(description = "User's password", example = "P@ssw0rd")
  private String password;

  @NonNull
  @Schema(description = "Phone number of the user", example = "+12345678901")
  private String phoneNumber;
}
