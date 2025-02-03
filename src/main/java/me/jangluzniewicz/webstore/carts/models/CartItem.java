package me.jangluzniewicz.webstore.carts.models;

import lombok.*;
import me.jangluzniewicz.webstore.products.models.Product;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class CartItem {
  private Long id;
  @NonNull private Product product;
  @NonNull private Integer quantity;
}
