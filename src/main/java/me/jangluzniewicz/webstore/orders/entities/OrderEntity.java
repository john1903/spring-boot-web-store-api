package me.jangluzniewicz.webstore.orders.entities;

import jakarta.persistence.*;
import lombok.*;
import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.users.entities.UserEntity;

import java.sql.Timestamp;
import java.util.List;

@Entity
@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
@Builder
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_date")
    private Timestamp orderDate;
    @Column(name = "status_change_date")
    private Timestamp statusChangeDate;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NonNull private UserEntity customer;
    @ManyToOne
    @JoinColumn(name = "order_status_id")
    private OrderStatusEntity status;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "rating_id")
    private RatingEntity rating;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> items;
}
