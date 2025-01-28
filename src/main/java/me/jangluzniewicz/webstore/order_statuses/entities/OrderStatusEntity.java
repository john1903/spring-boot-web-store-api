package me.jangluzniewicz.webstore.order_statuses.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "order_statuses")
public class OrderStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @NonNull
    private String name;
}
