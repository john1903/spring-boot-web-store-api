package me.jangluzniewicz.webstore.categories.interfaces;

import java.util.Optional;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.utils.models.PagedResponse;

public interface ICategory {
  Long createNewCategory(CategoryRequest categoryRequest);

  Optional<Category> getCategoryById(Long id);

  PagedResponse<Category> getAllCategories(Integer page, Integer size);

  Long updateCategory(Long id, CategoryRequest categoryRequest);

  void deleteCategory(Long id);
}
