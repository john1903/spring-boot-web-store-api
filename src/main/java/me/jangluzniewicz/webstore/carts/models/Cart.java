package me.jangluzniewicz.webstore.carts.models;

import java.util.List;
import lombok.*;
import me.jangluzniewicz.webstore.users.models.User;

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
  @NonNull private User customer;
  @NonNull private List<CartItem> items;
}
