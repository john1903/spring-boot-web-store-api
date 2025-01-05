package me.jangluzniewicz.webstore.users.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
@Builder
public class Role {
    private Long id;
    @NonNull
    @NotNull(message = "Name is required")
    private String name;
}
