package me.jangluzniewicz.webstore.users.entities;

import jakarta.persistence.*;
import lombok.*;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import org.hibernate.annotations.DynamicInsert;

@Entity
@DynamicInsert
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "users")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private RoleEntity role;

  @Column(nullable = false, unique = true)
  @NonNull
  private String email;

  @Column(nullable = false)
  @NonNull
  private String password;

  @Column(name = "phone_number", nullable = false, unique = true)
  @NonNull
  private String phoneNumber;
}
