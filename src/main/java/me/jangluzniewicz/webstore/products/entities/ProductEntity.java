package me.jangluzniewicz.webstore.products.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "products")
public class ProductEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @NonNull
  private String name;

  @Column(length = 5000)
  private String description;

  @Column(nullable = false)
  @NonNull
  private BigDecimal price;

  private BigDecimal weight;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  @NonNull
  private CategoryEntity category;
}
