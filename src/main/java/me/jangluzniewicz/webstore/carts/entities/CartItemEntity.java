package me.jangluzniewicz.webstore.carts.entities;

import jakarta.persistence.*;
import lombok.*;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;

@Entity
@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
@Table(name = "cart_items")
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NonNull private ProductEntity product;
    @Column(nullable = false)
    @NonNull private Integer quantity;
}
