package me.jangluzniewicz.webstore.carts.models;

import java.util.List;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Cart {
  private Long id;
  @NonNull private Long customerId;
  @NonNull private List<CartItem> items;
}
