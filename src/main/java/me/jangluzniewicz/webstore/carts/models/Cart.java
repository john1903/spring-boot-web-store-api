package me.jangluzniewicz.webstore.carts.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.jangluzniewicz.webstore.users.models.User;

import java.util.List;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
@Builder
public class Cart {
    private Long id;
    @NonNull
    @NotNull(message = "Customer is required")
    private User customer;
    private List<CartItem> items;
}
