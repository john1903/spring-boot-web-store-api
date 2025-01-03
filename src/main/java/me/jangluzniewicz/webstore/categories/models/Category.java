package me.jangluzniewicz.webstore.categories.models;

import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
public class Category {
    private Long id;
    @NonNull
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;
}
