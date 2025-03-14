package me.jangluzniewicz.webstore.categories.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.mappers.CategoryMapper;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.categories.repositories.CategoryRepository;
import me.jangluzniewicz.webstore.categories.services.CategoryService;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.utils.testdata.categories.CategoryEntityTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.categories.CategoryRequestTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.categories.CategoryTestDataBuilder;
import me.jangluzniewicz.webstore.utils.units.config.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class CategoryServiceTest extends UnitTest {
  @Mock private CategoryRepository categoryRepository;
  @Mock private CategoryMapper categoryMapper;
  @InjectMocks private CategoryService categoryService;

  private CategoryEntity categoryEntity;
  private Category category;
  private CategoryRequest categoryRequest1;
  private CategoryRequest categoryRequest2;

  @BeforeEach
  void setUp() {
    categoryEntity = CategoryEntityTestDataBuilder.builder().build().buildCategoryEntity();
    category = CategoryTestDataBuilder.builder().build().buildCategory();
    categoryRequest1 = CategoryRequestTestDataBuilder.builder().build().buildCategoryRequest();
    categoryRequest2 =
        CategoryRequestTestDataBuilder.builder().name("Clothes").build().buildCategoryRequest();
  }

  @Test
  void createNewCategory_whenNotExists_thenReturnIdResponse() {
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest1.getName())).thenReturn(false);
    when(categoryRepository.save(any())).thenReturn(categoryEntity);

    assertEquals(
        categoryEntity.getId(), categoryService.createNewCategory(categoryRequest1).getId());
  }

  @Test
  void createNewCategory_whenExists_thenThrowException() {
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest1.getName())).thenReturn(true);

    assertThrows(
        NotUniqueException.class, () -> categoryService.createNewCategory(categoryRequest1));
  }

  @Test
  void getCategoryById_whenExists_thenReturnCategory() {
    when(categoryRepository.findById(categoryEntity.getId()))
        .thenReturn(Optional.of(categoryEntity));
    when(categoryMapper.fromEntity(categoryEntity)).thenReturn(category);

    assertTrue(categoryService.getCategoryById(categoryEntity.getId()).isPresent());
  }

  @Test
  void getCategoryById_whenNotExists_thenReturnEmpty() {
    when(categoryRepository.findById(categoryEntity.getId())).thenReturn(Optional.empty());

    assertTrue(categoryService.getCategoryById(categoryEntity.getId()).isEmpty());
  }

  @Test
  void getAllCategories_whenExists_thenReturnPagedResponse() {
    when(categoryRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(categoryEntity)));
    when(categoryMapper.fromEntity(categoryEntity)).thenReturn(category);

    assertEquals(1, categoryService.getAllCategories(0, 10).getTotalPages());
  }

  @Test
  void updateCategory_whenExistsAndUnique_thenUpdateCategory() {
    when(categoryRepository.findById(categoryEntity.getId()))
        .thenReturn(Optional.of(categoryEntity));
    when(categoryMapper.fromEntity(categoryEntity)).thenReturn(category);
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest2.getName())).thenReturn(false);
    CategoryEntity updatedEntity =
        CategoryEntityTestDataBuilder.builder()
            .name(categoryRequest2.getName())
            .build()
            .buildCategoryEntity();
    when(categoryRepository.save(any())).thenReturn(updatedEntity);

    assertDoesNotThrow(
        () -> categoryService.updateCategory(categoryEntity.getId(), categoryRequest2));
  }

  @Test
  void updateCategory_whenExistsAndNotUnique_thenThrowException() {
    when(categoryRepository.findById(categoryEntity.getId()))
        .thenReturn(Optional.of(categoryEntity));
    when(categoryMapper.fromEntity(categoryEntity)).thenReturn(category);
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest2.getName())).thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () -> categoryService.updateCategory(categoryEntity.getId(), categoryRequest2));
  }

  @Test
  void updateCategory_whenNotExists_thenThrowException() {
    when(categoryRepository.findById(categoryEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> categoryService.updateCategory(categoryEntity.getId(), categoryRequest1));
  }

  @Test
  public void updateCategory_whenCategoryExistsAndNewNameIsSame_thenDoNotThrowException() {
    when(categoryRepository.findById(categoryEntity.getId()))
        .thenReturn(Optional.of(categoryEntity));
    when(categoryMapper.fromEntity(categoryEntity)).thenReturn(category);
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest1.getName())).thenReturn(true);
    when(categoryRepository.save(any())).thenReturn(categoryEntity);

    assertDoesNotThrow(
        () -> categoryService.updateCategory(categoryEntity.getId(), categoryRequest1));
  }

  @Test
  void deleteCategory_whenExists_thenDeleteSuccessfully() {
    when(categoryRepository.findById(categoryEntity.getId()))
        .thenReturn(Optional.of(categoryEntity));

    assertDoesNotThrow(() -> categoryService.deleteCategory(categoryEntity.getId()));
  }

  @Test
  void deleteCategory_whenNotExists_thenThrowException() {
    when(categoryRepository.findById(categoryEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> categoryService.deleteCategory(categoryEntity.getId()));
  }
}
