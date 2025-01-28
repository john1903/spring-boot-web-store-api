package me.jangluzniewicz.webstore.users.repositories;

import me.jangluzniewicz.webstore.users.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    boolean existsByNameLike(String name);
}
