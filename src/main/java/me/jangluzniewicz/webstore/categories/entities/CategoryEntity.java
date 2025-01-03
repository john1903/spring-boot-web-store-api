package me.jangluzniewicz.webstore.categories.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor @Getter @Setter @ToString
@Table(name = "categories")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    public CategoryEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryEntity(String name) {
        this.name = name;
    }
}
