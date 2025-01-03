package me.jangluzniewicz.webstore.order_statuses.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor @Getter @Setter @ToString
@Table(name = "order_statuses")
public class OrderStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    public OrderStatusEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public OrderStatusEntity(String name) {
        this.name = name;
    }
}
