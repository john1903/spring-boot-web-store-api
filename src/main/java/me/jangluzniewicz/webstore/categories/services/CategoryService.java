package me.jangluzniewicz.webstore.categories.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.jangluzniewicz.webstore.categories.controllers.CategoryRequest;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.mappers.CategoryMapper;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.categories.repositories.CategoryRepository;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

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
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new NotUniqueException("Category with name " + categoryRequest.getName() + " already exists");
        }
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();
        return categoryRepository.save(categoryMapper.toEntity(category)).getId();
    }

    @Override
    public Optional<Category> getCategoryById(@Min(1) Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::fromEntity);
    }

    @Override
    public List<Category> getAllCategories() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
                .map(categoryMapper::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public Long updateCategory(@Min(1) Long id, @NotNull CategoryRequest categoryRequest) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
        if (categoryRepository.existsByName(categoryRequest.getName()) &&
                !categoryEntity.getName().equals(categoryRequest.getName())) {
            throw new NotUniqueException("Category with name " + categoryRequest.getName() + " already exists");
        }
        categoryEntity.setName(categoryRequest.getName());
        return categoryEntity.getId();
    }

    @Override
    public void deleteCategory(@Min(1) Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category with id " + id + " not found");
        }
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new DeletionNotAllowedException("Category with id " + id +
                        " cannot be deleted due to existing relations");
            }
        }
    }
}
