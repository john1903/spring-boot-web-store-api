package me.jangluzniewicz.webstore.products.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import me.jangluzniewicz.webstore.categories.models.Category;

import java.math.BigDecimal;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
@Builder
public class Product {
    private Long id;
    @NonNull
    @NotNull(message = "Name is required")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;
    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;
    @NonNull
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;
    @DecimalMin(value = "0.0", message = "Weight cannot be negative")
    private BigDecimal weight;
    @NonNull
    @NotNull(message = "Category is required")
    private Category category;
}
