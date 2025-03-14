package me.jangluzniewicz.webstore.orders.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import me.jangluzniewicz.webstore.orderstatuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Formula;

@Entity
@DynamicInsert
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "orders")
public class OrderEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_date")
  private LocalDateTime orderDate;

  @Column(name = "status_change_date")
  private LocalDateTime statusChangeDate;

  @ManyToOne
  @JoinColumn(name = "customer_id", nullable = false)
  @NonNull
  private UserEntity customer;

  private String email;

  @Column(name = "phone_number")
  private String phoneNumber;

  @ManyToOne
  @JoinColumn(name = "order_status_id")
  private OrderStatusEntity status;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "rating_id")
  private RatingEntity rating;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "order_id")
  private List<OrderItemEntity> items;

  @Formula(
      "(SELECT COALESCE(SUM(oi.price * oi.quantity), 0.00) FROM order_items oi WHERE oi.order_id = id)")
  private BigDecimal total;
}
