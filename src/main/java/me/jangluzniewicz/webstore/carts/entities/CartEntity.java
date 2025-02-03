package me.jangluzniewicz.webstore.carts.entities;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import me.jangluzniewicz.webstore.users.entities.UserEntity;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "carts")
public class CartEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "customer_id", nullable = false)
  @NonNull
  private UserEntity customer;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "cart_id")
  private List<CartItemEntity> items;
}
