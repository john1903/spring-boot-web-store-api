package me.jangluzniewicz.webstore.users.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @NonNull private RoleEntity role;
    @Column(nullable = false, unique = true)
    @NonNull private String email;
    @Column(nullable = false)
    @NonNull private String password;
    @Column(name = "phone_number", nullable = false, unique = true)
    @NonNull private String phoneNumber;
}
