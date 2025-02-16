package me.jangluzniewicz.webstore.categories.interfaces;

import java.util.Optional;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;

public interface ICategory {
  IdResponse createNewCategory(CategoryRequest categoryRequest);

  Optional<Category> getCategoryById(Long id);

  PagedResponse<Category> getAllCategories(Integer page, Integer size);

  void updateCategory(Long id, CategoryRequest categoryRequest);

  void deleteCategory(Long id);
}
