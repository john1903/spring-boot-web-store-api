package me.jangluzniewicz.webstore.users.models;

import lombok.*;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
public class Role {
    private Long id;
    @NonNull
    private String name;
}
