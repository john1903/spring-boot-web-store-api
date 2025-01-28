package me.jangluzniewicz.webstore.categories.interfaces;

import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.models.Category;

import java.util.List;
import java.util.Optional;

public interface ICategory {
    Long createNewCategory(CategoryRequest categoryRequest);

    Optional<Category> getCategoryById(Long id);

    List<Category> getAllCategories(Integer page, Integer size);

    Long updateCategory(Long id, CategoryRequest categoryRequest);

    void deleteCategory(Long id);
}
