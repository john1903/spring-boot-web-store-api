package me.jangluzniewicz.webstore.products.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.common.units.BaseServiceTest;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.mappers.ProductMapper;
import me.jangluzniewicz.webstore.products.repositories.ProductRepository;
import me.jangluzniewicz.webstore.products.services.ProductService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest extends BaseServiceTest {
  @Mock private ProductRepository productRepository;
  @Mock private ProductMapper productMapper;
  @Mock private ICategory categoryService;
  @InjectMocks private ProductService productService;

  private ProductRequest productRequest;
  private ProductRequest productRequest2;
  private ProductFilterRequest productFilterRequest;

  @BeforeEach
  void setUp() {
    productRequest =
        new ProductRequest(
            "Bicycle", "Mountain bike", BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), 1L);

    productRequest2 =
        new ProductRequest(
            "Bicycle XXL",
            "Mountain bike",
            BigDecimal.valueOf(1200.0),
            BigDecimal.valueOf(10.0),
            1L);

    productFilterRequest =
        new ProductFilterRequest(1L, "Bicycle", BigDecimal.valueOf(0), BigDecimal.valueOf(2000.0));
  }

  @Test
  void createNewProduct_whenCategoryExists_thenReturnProductId() {
    when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
    when(productMapper.toEntity(any())).thenReturn(productEntity);
    when(productRepository.save(any())).thenReturn(productEntity);

    assertEquals(1L, productService.createNewProduct(productRequest));
  }

  @Test
  void createNewProduct_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productService.createNewProduct(productRequest));
  }

  @Test
  void getProductById_whenProductExists_thenReturnProduct() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(any())).thenReturn(product);

    assertTrue(productService.getProductById(1L).isPresent());
  }

  @Test
  void getProductById_whenProductDoesNotExist_thenReturnEmpty() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertTrue(productService.getProductById(1L).isEmpty());
  }

  @Test
  void getAllProducts_whenProductsExist_thenReturnPagedResponse() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);
    when(productRepository.findAll(pageable)).thenReturn(page);
    when(productMapper.fromEntity(any())).thenReturn(product);

    assertEquals(1, productService.getAllProducts(0, 10).getTotalPages());
  }

  @Test
  void getFilteredProducts_whenProductsExist_thenReturnPagedResponse() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);
    when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    when(productMapper.fromEntity(any())).thenReturn(product);

    assertEquals(
        1, productService.getFilteredProducts(productFilterRequest, 0, 10).getTotalPages());
  }

  @Test
  void updateProduct_whenProductExistsAndCategoryExists_thenReturnProductId() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(any())).thenReturn(product);
    when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
    ProductEntity updatedEntity =
        ProductEntity.builder()
            .id(1L)
            .name(productRequest2.getName())
            .description(productRequest2.getDescription())
            .price(productRequest2.getPrice())
            .weight(productRequest2.getWeight())
            .category(categoryEntity)
            .build();
    when(productRepository.save(any())).thenReturn(updatedEntity);

    assertEquals(1L, productService.updateProduct(1L, productRequest2));
  }

  @Test
  void updateProduct_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productRequest2));
  }

  @Test
  void updateProduct_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(any())).thenReturn(product);
    when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productRequest2));
  }

  @Test
  void deleteProduct_whenProductExists_thenDeleteSuccessfully() {
    when(productRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> productService.deleteProduct(1L));
  }

  @Test
  void deleteProduct_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(productRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
  }

  @Test
  void deleteProduct_whenProductHasDependencies_thenThrowDeletionNotAllowedException() {
    when(productRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(productRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> productService.deleteProduct(1L));
  }

  @Test
  void deleteProduct_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(productRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(productRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> productService.deleteProduct(1L));
  }
}
