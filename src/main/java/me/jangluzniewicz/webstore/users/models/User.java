package me.jangluzniewicz.webstore.users.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
@Builder
public class User {
    private Long id;
    @NonNull
    @NotNull(message = "Role is required")
    private Role role;
    @NonNull
    @NotNull(message = "First name is required")
    @Size(min = 1, max = 255, message = "Email must be between 1 and 255 characters")
    @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email format")
    private String email;
    @NonNull
    @NotNull(message = "Password is required")
    private String password;
    @NonNull
    @NotNull(message = "First name is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number format")
    private String phoneNumber;
}
