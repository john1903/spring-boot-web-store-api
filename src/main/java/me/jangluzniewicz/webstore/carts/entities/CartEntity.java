package me.jangluzniewicz.webstore.carts.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.Formula;

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

  @Formula("(SELECT SUM(ci.price * ci.quantity) FROM cart_items ci WHERE ci.cart_id = id)")
  private BigDecimal total;
}
