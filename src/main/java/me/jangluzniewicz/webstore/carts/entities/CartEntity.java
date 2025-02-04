package me.jangluzniewicz.webstore.carts.entities;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

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

  @NonNull
  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "cart_id")
  private List<CartItemEntity> items;
}
