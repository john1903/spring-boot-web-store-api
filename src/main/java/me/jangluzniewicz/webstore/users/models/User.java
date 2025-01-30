package me.jangluzniewicz.webstore.users.models;

import lombok.*;
import me.jangluzniewicz.webstore.roles.models.Role;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class User {
    private Long id;
    @NonNull
    private Role role;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String phoneNumber;
}
