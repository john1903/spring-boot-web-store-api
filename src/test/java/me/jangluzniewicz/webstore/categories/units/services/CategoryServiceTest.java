package me.jangluzniewicz.webstore.categories.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
  @Mock private CategoryRepository categoryRepository;
  @Mock private CategoryMapper categoryMapper;
  @InjectMocks private CategoryService categoryService;

  @Test
  void createNewCategory_whenNotExists_thenReturnId() {
    when(categoryRepository.existsByNameIgnoreCase("Groceries")).thenReturn(false);
    when(categoryRepository.save(any())).thenReturn(new CategoryEntity(1L, "Groceries"));

    assertEquals(1L, categoryService.createNewCategory(new CategoryRequest("Groceries")));
  }

  @Test
  void createNewCategory_whenExists_thenThrowException() {
    when(categoryRepository.existsByNameIgnoreCase("Groceries")).thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () -> categoryService.createNewCategory(new CategoryRequest("Groceries")));
  }

  @Test
  void getCategoryById_whenExists_thenReturnCategory() {
    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(new CategoryEntity(1L, "Groceries")));
    when(categoryMapper.fromEntity(any())).thenReturn(new Category(1L, "Groceries"));

    assertTrue(categoryService.getCategoryById(1L).isPresent());
  }

  @Test
  void getCategoryById_whenNotExists_thenReturnEmpty() {
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    assertTrue(categoryService.getCategoryById(1L).isEmpty());
  }

  @Test
  void getAllCategories_whenExists_thenReturnPagedResponse() {
    Page<CategoryEntity> page =
        new PageImpl<>(List.of(new CategoryEntity(1L, "Groceries")), PageRequest.of(0, 10), 1);

    when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(categoryMapper.fromEntity(any())).thenReturn(new Category(1L, "Groceries"));

    assertEquals(1, categoryService.getAllCategories(0, 10).getTotalPages());
  }

  @Test
  void updateCategory_whenExistsAndUnique_thenReturnId() {
    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(new CategoryEntity(1L, "Clothes")));
    when(categoryMapper.fromEntity(any())).thenReturn(new Category(1L, "Clothes"));
    when(categoryRepository.existsByNameIgnoreCase("Groceries")).thenReturn(false);
    when(categoryRepository.save(any())).thenReturn(new CategoryEntity(1L, "Groceries"));

    assertEquals(1L, categoryService.updateCategory(1L, new CategoryRequest("Groceries")));
  }

  @Test
  void updateCategory_whenExistsAndNotUnique_thenThrowException() {
    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(new CategoryEntity(1L, "Clothes")));
    when(categoryMapper.fromEntity(any())).thenReturn(new Category(1L, "Clothes"));
    when(categoryRepository.existsByNameIgnoreCase("Groceries")).thenReturn(true);

    assertThrows(
        NotUniqueException.class,
        () -> categoryService.updateCategory(1L, new CategoryRequest("Groceries")));
  }

  @Test
  void updateCategory_whenNotExists_thenThrowException() {
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> categoryService.updateCategory(1L, new CategoryRequest("Groceries")));
  }

  @Test
  public void updateCategory_whenCategoryExistsAndNewNameIsSame_thenDoNotThrowException() {
    when(categoryRepository.findById(1L))
        .thenReturn(Optional.of(new CategoryEntity(1L, "Clothes")));
    when(categoryMapper.fromEntity(any())).thenReturn(new Category(1L, "Clothes"));
    when(categoryRepository.existsByNameIgnoreCase("Clothes")).thenReturn(true);
    when(categoryRepository.save(any())).thenReturn(new CategoryEntity(1L, "Clothes"));

    assertDoesNotThrow(() -> categoryService.updateCategory(1L, new CategoryRequest("Clothes")));
  }

  @Test
  void deleteCategory_whenExists_thenDeleteSuccessfully() {
    when(categoryRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
  }

  @Test
  void deleteCategory_whenNotExists_thenThrowException() {
    when(categoryRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(1L));
  }

  @Test
  void deleteCategory_whenHasDependencies_thenThrowException() {
    when(categoryRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(categoryRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> categoryService.deleteCategory(1L));
  }

  @Test
  void deleteCategory_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(categoryRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(categoryRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> categoryService.deleteCategory(1L));
  }
}
