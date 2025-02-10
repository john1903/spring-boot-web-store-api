package me.jangluzniewicz.webstore.carts.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
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

  private BigDecimal total;

  @PrePersist
  @PreUpdate
  private void calculateTotal() {
    total =
        items.stream()
            .map(
                cartItemEntity ->
                    cartItemEntity
                        .getProduct()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(cartItemEntity.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
