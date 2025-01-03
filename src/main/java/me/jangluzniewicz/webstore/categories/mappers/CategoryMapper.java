package me.jangluzniewicz.webstore.categories.mappers;

import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category fromEntity(CategoryEntity entity);
    CategoryEntity toEntity(Category model);
}
