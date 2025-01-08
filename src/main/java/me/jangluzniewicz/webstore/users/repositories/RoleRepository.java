package me.jangluzniewicz.webstore.users.repositories;

import me.jangluzniewicz.webstore.users.entities.RoleEntity;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    boolean existsByNameLike(String name);
}
