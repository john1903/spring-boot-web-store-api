package me.jangluzniewicz.webstore.orders.models;

import lombok.*;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import me.jangluzniewicz.webstore.users.models.User;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Order {
    private Long id;
    private LocalDateTime orderDate;
    private LocalDateTime statusChangeDate;
    @NonNull
    private User customer;
    private OrderStatus status;
    private Rating rating;
    private List<OrderItem> items;
}
