package me.jangluzniewicz.webstore.categories.repositories;

import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
}
