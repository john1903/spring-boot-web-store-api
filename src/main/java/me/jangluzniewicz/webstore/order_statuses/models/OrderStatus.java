package me.jangluzniewicz.webstore.order_statuses.models;

import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
public class OrderStatus {
    private Long id;
    @NonNull
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;
}
