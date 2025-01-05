package me.jangluzniewicz.webstore.orders.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
@Builder
@Table(name = "ratings")
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NonNull private Integer rating;
    @Column(nullable = false, length = 500)
    @NonNull private String description;
}
