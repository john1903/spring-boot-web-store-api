package me.jangluzniewicz.webstore.users.repositories;

import java.util.Optional;
import me.jangluzniewicz.webstore.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);
}
