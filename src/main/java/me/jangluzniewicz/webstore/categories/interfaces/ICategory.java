package me.jangluzniewicz.webstore.categories.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;

public interface ICategory {
  IdResponse createNewCategory(@NotNull CategoryRequest categoryRequest);

  Optional<Category> getCategoryById(@NotNull @Min(1) Long id);

  PagedResponse<Category> getAllCategories(
      @NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  void updateCategory(@NotNull @Min(1) Long id, @NotNull CategoryRequest categoryRequest);

  void deleteCategory(@NotNull @Min(1) Long id);
}
