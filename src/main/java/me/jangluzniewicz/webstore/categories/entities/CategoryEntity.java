package me.jangluzniewicz.webstore.categories.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
@Table(name = "categories")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @NonNull private String name;
}
