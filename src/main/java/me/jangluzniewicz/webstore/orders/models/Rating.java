package me.jangluzniewicz.webstore.orders.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
public class Rating {
    private Long id;
    @NonNull
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    @NonNull
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;
}
