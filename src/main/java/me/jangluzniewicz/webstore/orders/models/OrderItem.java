package me.jangluzniewicz.webstore.orders.models;

import jakarta.validation.constraints.*;
import lombok.*;
import me.jangluzniewicz.webstore.products.models.Product;

import java.math.BigDecimal;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
@Builder
public class OrderItem {
    private Long id;
    @NonNull
    @NotNull(message = "Product is required")
    private Product product;
    @NonNull
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity must be at most 100")
    private Integer quantity;
    @NonNull
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;
    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    @DecimalMax(value = "1.0", message = "Discount must be at most 1")
    private BigDecimal discount;
}
