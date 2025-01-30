package me.jangluzniewicz.webstore.roles.repositories;

import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
}
