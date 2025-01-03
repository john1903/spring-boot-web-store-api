package me.jangluzniewicz.webstore.carts.models;

import lombok.*;
import me.jangluzniewicz.webstore.users.models.User;

import java.util.List;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
public class Cart {
    private Long id;
    @NonNull
    private User customer;
    private List<CartItem> items;
}
