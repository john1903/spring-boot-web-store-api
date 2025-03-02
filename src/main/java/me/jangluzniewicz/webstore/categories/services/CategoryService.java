package me.jangluzniewicz.webstore.categories.services;

import jakarta.transaction.Transactional;
import java.util.Optional;
import me.jangluzniewicz.webstore.aws.interfaces.IAwsS3;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.mappers.CategoryMapper;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.categories.repositories.CategoryRepository;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CategoryService implements ICategory {
  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;
  private final IAwsS3 awsS3;

  public CategoryService(
      CategoryRepository categoryRepository, CategoryMapper categoryMapper, IAwsS3 awsS3) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
    this.awsS3 = awsS3;
  }

  @Override
  @Transactional
  public IdResponse createNewCategory(CategoryRequest categoryRequest) {
    if (categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())) {
      throw new NotUniqueException(
          "Category with name " + categoryRequest.getName() + " already exists");
    }
    Category category = Category.builder().name(categoryRequest.getName()).build();
    return new IdResponse(categoryRepository.save(categoryMapper.toEntity(category)).getId());
  }

  @Override
  public Optional<Category> getCategoryById(Long id) {
    return categoryRepository
        .findById(id)
        .map(
            categoryEntity -> {
              var category = categoryMapper.fromEntity(categoryEntity);
              category.setImageUrl(
                  category.getImageUri() != null && !category.getImageUri().isEmpty()
                      ? awsS3.getSignedUrl(category.getImageUri())
                      : "");
              return category;
            });
  }

  @Override
  public PagedResponse<Category> getAllCategories(Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Category> categories =
        categoryRepository
            .findAll(pageable)
            .map(
                categoryEntity -> {
                  var category = categoryMapper.fromEntity(categoryEntity);
                  category.setImageUrl(
                      category.getImageUri() != null && !category.getImageUri().isEmpty()
                          ? awsS3.getSignedUrl(category.getImageUri())
                          : "");
                  return category;
                });
    return new PagedResponse<>(categories.getTotalPages(), categories.toList());
  }

  @Override
  @Transactional
  public void updateCategory(Long id, CategoryRequest categoryRequest) {
    Category category =
        getCategoryById(id)
            .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    if (categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())
        && !category.getName().equals(categoryRequest.getName())) {
      throw new NotUniqueException(
          "Category with name " + categoryRequest.getName() + " already exists");
    }
    category.setName(categoryRequest.getName());
    categoryRepository.save(categoryMapper.toEntity(category));
  }

  @Override
  public void deleteCategory(Long id) {
    if (!categoryRepository.existsById(id)) {
      throw new NotFoundException("Category with id " + id + " not found");
    }
    categoryRepository.deleteById(id);
  }
}
