package me.jangluzniewicz.webstore.categories.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.mappers.CategoryMapper;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.categories.repositories.CategoryRepository;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService implements ICategory {
  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Override
  @Transactional
  public Long createNewCategory(@NotNull CategoryRequest categoryRequest) {
    if (categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())) {
      throw new NotUniqueException(
          "Category with name " + categoryRequest.getName() + " already exists");
    }
    Category category = Category.builder().name(categoryRequest.getName()).build();
    return categoryRepository.save(categoryMapper.toEntity(category)).getId();
  }

  @Override
  public Optional<Category> getCategoryById(@NotNull @Min(1) Long id) {
    return categoryRepository.findById(id).map(categoryMapper::fromEntity);
  }

  @Override
  public PagedResponse<Category> getAllCategories(
      @NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Category> categories =
        categoryRepository.findAll(pageable).map(categoryMapper::fromEntity);
    return new PagedResponse<>(categories.getTotalPages(), categories.toList());
  }

  @Override
  @Transactional
  public Long updateCategory(@NotNull @Min(1) Long id, @NotNull CategoryRequest categoryRequest) {
    Category category =
        getCategoryById(id)
            .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    if (categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())
        && !category.getName().equals(categoryRequest.getName())) {
      throw new NotUniqueException(
          "Category with name " + categoryRequest.getName() + " already exists");
    }
    category.setName(categoryRequest.getName());
    return categoryRepository.save(categoryMapper.toEntity(category)).getId();
  }

  @Override
  public void deleteCategory(@NotNull @Min(1) Long id) {
    if (!categoryRepository.existsById(id)) {
      throw new NotFoundException("Category with id " + id + " not found");
    }
    try {
      categoryRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException) {
        throw new DeletionNotAllowedException(
            "Category with id " + id + " cannot be deleted due to existing relations");
      } else {
        throw e;
      }
    }
  }
}
