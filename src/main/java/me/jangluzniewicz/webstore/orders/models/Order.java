package me.jangluzniewicz.webstore.orders.models;

import lombok.*;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.users.models.User;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode
public class Order {
    private Long id;
    private Timestamp orderDate;
    private Timestamp statusChangeDate;
    @NonNull private User customer;
    private OrderStatus status;
    private Rating rating;
    private List<OrderItem> items;
}
