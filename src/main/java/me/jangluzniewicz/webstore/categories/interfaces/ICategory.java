package me.jangluzniewicz.webstore.categories.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;

/** Interface for managing categories */
public interface ICategory {

  /**
   * Creates a new category.
   *
   * @param categoryRequest the request object containing the details of the category to be created;
   *     must not be null.
   * @return an {@link IdResponse} containing the ID of the newly created category.
   */
  IdResponse createNewCategory(@NotNull CategoryRequest categoryRequest);

  /**
   * Retrieves a category by its ID.
   *
   * @param id the ID of the category to be retrieved; must be a positive number.
   * @return an {@link Optional} containing the {@link Category} if found, or empty if not found.
   */
  Optional<Category> getCategoryById(@NotNull @Min(1) Long id);

  /**
   * Retrieves all categories with pagination.
   *
   * @param page the page number to retrieve; must be a non-negative number.
   * @param size the number of categories per page; must be a positive number.
   * @return a {@link PagedResponse} containing the paginated list of categories.
   */
  PagedResponse<Category> getAllCategories(
      @NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size);

  /**
   * Updates an existing category.
   *
   * @param id the ID of the category to be updated; must be a positive number.
   * @param categoryRequest the request object containing the updated details of the category; must
   *     not be null.
   */
  void updateCategory(@NotNull @Min(1) Long id, @NotNull CategoryRequest categoryRequest);

  /**
   * Deletes a category by its ID.
   *
   * @param id the ID of the category to be deleted; must be a positive number.
   */
  void deleteCategory(@NotNull @Min(1) Long id);
}
