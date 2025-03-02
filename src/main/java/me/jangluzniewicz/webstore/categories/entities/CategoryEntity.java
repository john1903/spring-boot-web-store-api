package me.jangluzniewicz.webstore.categories.entities;

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
@Table(name = "categories")
public class CategoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "image_uri")
  private String imageUri;

  @Column(nullable = false, unique = true)
  @NonNull
  private String name;
}
