package me.jangluzniewicz.webstore.orders.models;

import lombok.*;
import me.jangluzniewicz.webstore.products.models.Product;

import java.math.BigDecimal;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class OrderItem {
    private Long id;
    @NonNull
    private Product product;
    @NonNull
    private Integer quantity;
    @NonNull
    private BigDecimal price;
    private BigDecimal discount;
}
