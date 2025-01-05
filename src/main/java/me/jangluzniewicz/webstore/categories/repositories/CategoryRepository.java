package me.jangluzniewicz.webstore.categories.repositories;

import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<CategoryEntity, Long> {
    boolean existsByName(String name);
}
