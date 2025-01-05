package me.jangluzniewicz.webstore.carts.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.jangluzniewicz.webstore.products.models.Product;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
@Builder
public class CartItem {
    private Long id;
    @NonNull
    @NotNull(message = "Product is required")
    private Product product;
    @NonNull
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity must be at most 100")
    private Integer quantity;
}
