package me.jangluzniewicz.webstore.categories.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.mappers.CategoryMapper;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.categories.repositories.CategoryRepository;
import me.jangluzniewicz.webstore.categories.services.CategoryService;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.common.testdata.categories.CategoryEntityTestDataBuilder;
import me.jangluzniewicz.webstore.common.testdata.categories.CategoryRequestTestDataBuilder;
import me.jangluzniewicz.webstore.common.testdata.categories.CategoryTestDataBuilder;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
  @Mock private CategoryRepository categoryRepository;
  @Mock private CategoryMapper categoryMapper;
  @InjectMocks private CategoryService categoryService;

  private CategoryEntity categoryEntity;
  private Category category;
  private CategoryRequest categoryRequest1;
  private CategoryRequest categoryRequest2;

  @BeforeEach
  void setUp() {
    categoryEntity = CategoryEntityTestDataBuilder.builder().id(1L).build().buildCategoryEntity();
    category = CategoryTestDataBuilder.builder().id(1L).build().buildCategory();
    categoryRequest1 = CategoryRequestTestDataBuilder.builder().build().buildCategoryRequest();
    categoryRequest2 =
        CategoryRequestTestDataBuilder.builder().name("Clothes").build().buildCategoryRequest();
  }

  @Test
  void createNewCategory_whenNotExists_thenReturnId() {
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest1.getName())).thenReturn(false);
    when(categoryRepository.save(any())).thenReturn(categoryEntity);

    assertEquals(categoryEntity.getId(), categoryService.createNewCategory(categoryRequest1));
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
  void updateCategory_whenExistsAndUnique_thenReturnId() {
    when(categoryRepository.findById(categoryEntity.getId()))
        .thenReturn(Optional.of(categoryEntity));
    when(categoryMapper.fromEntity(categoryEntity)).thenReturn(category);
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest2.getName())).thenReturn(false);
    CategoryEntity updatedEntity =
        CategoryEntityTestDataBuilder.builder()
            .id(categoryEntity.getId())
            .name(categoryRequest2.getName())
            .build()
            .buildCategoryEntity();
    when(categoryRepository.save(any())).thenReturn(updatedEntity);

    assertEquals(
        categoryEntity.getId(),
        categoryService.updateCategory(categoryEntity.getId(), categoryRequest2));
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
    when(categoryRepository.existsById(categoryEntity.getId())).thenReturn(true);

    assertDoesNotThrow(() -> categoryService.deleteCategory(categoryEntity.getId()));
  }

  @Test
  void deleteCategory_whenNotExists_thenThrowException() {
    when(categoryRepository.existsById(categoryEntity.getId())).thenReturn(false);

    assertThrows(
        NotFoundException.class, () -> categoryService.deleteCategory(categoryEntity.getId()));
  }

  @Test
  void deleteCategory_whenHasDependencies_thenThrowException() {
    when(categoryRepository.existsById(categoryEntity.getId())).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(categoryRepository)
        .deleteById(categoryEntity.getId());

    assertThrows(
        DeletionNotAllowedException.class,
        () -> categoryService.deleteCategory(categoryEntity.getId()));
  }

  @Test
  void deleteCategory_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(categoryRepository.existsById(categoryEntity.getId())).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(categoryRepository)
        .deleteById(categoryEntity.getId());

    assertThrows(
        DataIntegrityViolationException.class,
        () -> categoryService.deleteCategory(categoryEntity.getId()));
  }
}
