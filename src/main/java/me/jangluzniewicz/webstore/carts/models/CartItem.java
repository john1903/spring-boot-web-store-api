package me.jangluzniewicz.webstore.carts.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import me.jangluzniewicz.webstore.products.models.Product;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
public class CartItem {
    private Long id;
    @NonNull
    private Product product;
    @NonNull
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity must be at most 100")
    private Integer quantity;
}
