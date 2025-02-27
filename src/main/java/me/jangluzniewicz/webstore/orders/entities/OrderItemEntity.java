package me.jangluzniewicz.webstore.orders.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "order_items")
public class OrderItemEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  @NonNull
  private ProductEntity product;

  @Column(nullable = false)
  @NonNull
  private Integer quantity;

  @Column(nullable = false)
  @NonNull
  private BigDecimal price;
}
