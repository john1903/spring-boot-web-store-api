package me.jangluzniewicz.webstore.categories.units.services;

import static org.junit.jupiter.api.Assertions.*;
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
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
  @Mock private CategoryRepository categoryRepository;
  @Mock private CategoryMapper categoryMapper;
  @InjectMocks private CategoryService categoryService;

  @Test
  public void createNewCategory_whenCategoryDoesNotExist_thenReturnCategoryId() {
    CategoryRequest categoryRequest = new CategoryRequest("Groceries");
    CategoryEntity savedEntity = new CategoryEntity(1L, "Groceries");

    when(categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())).thenReturn(false);
    when(categoryMapper.toEntity(any())).thenReturn(new CategoryEntity(null, "Groceries"));
    when(categoryRepository.save(any())).thenReturn(savedEntity);

    Long categoryId = categoryService.createNewCategory(categoryRequest);

    assertEquals(1L, categoryId);
  }

  @Test
  public void createNewCategory_whenCategoryAlreadyExists_thenThrowNotUniqueException() {
    CategoryRequest categoryRequest = new CategoryRequest("Groceries");

    when(categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())).thenReturn(true);

    assertThrows(
        NotUniqueException.class, () -> categoryService.createNewCategory(categoryRequest));
  }

  @Test
  public void getCategoryById_whenCategoryExists_thenReturnCategory() {
    CategoryEntity categoryEntity = new CategoryEntity(1L, "Groceries");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
    when(categoryMapper.fromEntity(categoryEntity)).thenReturn(new Category(1L, "Groceries"));

    Optional<Category> category = categoryService.getCategoryById(1L);

    assertTrue(category.isPresent());
    assertEquals(1L, category.get().getId());
    assertEquals("Groceries", category.get().getName());
  }

  @Test
  public void getCategoryById_whenCategoryDoesNotExist_thenReturnEmpty() {
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<Category> category = categoryService.getCategoryById(1L);

    assertTrue(category.isEmpty());
  }

  @Test
  public void getAllCategories_whenCategoriesExist_thenReturnPagedResponse() {
    CategoryEntity categoryEntity = new CategoryEntity(1L, "Groceries");
    Pageable pageable = PageRequest.of(0, 10);
    Page<CategoryEntity> page = new PageImpl<>(List.of(categoryEntity), pageable, 1);

    when(categoryRepository.findAll(pageable)).thenReturn(page);
    when(categoryMapper.fromEntity(categoryEntity)).thenReturn(new Category(1L, "Groceries"));

    PagedResponse<Category> pagedResponse = categoryService.getAllCategories(0, 10);

    assertEquals(1, pagedResponse.getTotalPages());
    assertEquals(1, pagedResponse.getContent().size());
    assertEquals(1L, pagedResponse.getContent().getFirst().getId());
    assertEquals("Groceries", pagedResponse.getContent().getFirst().getName());
  }

  @Test
  public void updateCategory_whenCategoryExistsAndNewNameIsUnique_thenReturnCategoryId() {
    CategoryRequest categoryRequest = new CategoryRequest("Groceries");
    CategoryEntity categoryEntity = new CategoryEntity(1L, "Clothes");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())).thenReturn(false);

    Long categoryId = categoryService.updateCategory(1L, categoryRequest);

    assertEquals(1L, categoryId);
  }

  @Test
  public void
      updateCategory_whenCategoryExistsAndNewNameAlreadyExists_thenThrowNotUniqueException() {
    CategoryRequest categoryRequest = new CategoryRequest("Groceries");
    CategoryEntity categoryEntity = new CategoryEntity(1L, "Clothes");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())).thenReturn(true);

    assertThrows(
        NotUniqueException.class, () -> categoryService.updateCategory(1L, categoryRequest));
  }

  @Test
  public void updateCategory_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    CategoryRequest categoryRequest = new CategoryRequest("Groceries");

    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> categoryService.updateCategory(1L, categoryRequest));
  }

  @Test
  public void updateCategory_whenCategoryExistsAndNewNameIsSame_thenDoNotThrowException() {
    CategoryRequest categoryRequest = new CategoryRequest("Clothes");
    CategoryEntity categoryEntity = new CategoryEntity(1L, "Clothes");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
    when(categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())).thenReturn(true);

    assertDoesNotThrow(() -> categoryService.updateCategory(1L, categoryRequest));
  }

  @Test
  public void deleteCategory_whenCategoryExists_thenDeleteSuccessfully() {
    when(categoryRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
  }

  @Test
  public void deleteCategory_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    when(categoryRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(1L));
  }

  @Test
  public void deleteCategory_whenCategoryHasDependencies_thenThrowDeletionNotAllowedException() {
    when(categoryRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(categoryRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> categoryService.deleteCategory(1L));
  }

  @Test
  public void deleteCategory_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(categoryRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(categoryRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> categoryService.deleteCategory(1L));
  }
}
