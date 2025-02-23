package me.jangluzniewicz.webstore.products.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.commons.testdata.categories.CategoryTestDataBuilder;
import me.jangluzniewicz.webstore.commons.testdata.products.ProductEntityTestDataBuilder;
import me.jangluzniewicz.webstore.commons.testdata.products.ProductFilterRequestTestDataBuilder;
import me.jangluzniewicz.webstore.commons.testdata.products.ProductRequestTestDataBuilder;
import me.jangluzniewicz.webstore.commons.testdata.products.ProductTestDataBuilder;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.mappers.ProductMapper;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.products.repositories.ProductRepository;
import me.jangluzniewicz.webstore.products.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  @Mock private ProductRepository productRepository;
  @Mock private ProductMapper productMapper;
  @Mock private ICategory categoryService;
  @InjectMocks private ProductService productService;

  private ProductEntity productEntity;
  private Product product;
  private Category category;
  private ProductRequest productRequest;
  private ProductRequest productRequest2;
  private ProductFilterRequest productFilterRequest;

  @BeforeEach
  void setUp() {
    productEntity = ProductEntityTestDataBuilder.builder().build().buildProductEntity();
    product = ProductTestDataBuilder.builder().build().buildProduct();
    category = CategoryTestDataBuilder.builder().build().buildCategory();
    productRequest = ProductRequestTestDataBuilder.builder().build().buildProductRequest();
    productRequest2 =
        ProductRequestTestDataBuilder.builder().name("Bicycle XXL").build().buildProductRequest();
    productFilterRequest =
        ProductFilterRequestTestDataBuilder.builder().build().buildProductFilterRequest();
  }

  @Test
  void createNewProduct_whenCategoryExists_thenReturnIdResponse() {
    when(categoryService.getCategoryById(category.getId())).thenReturn(Optional.of(category));
    when(productRepository.save(any())).thenReturn(productEntity);

    assertEquals(productEntity.getId(), productService.createNewProduct(productRequest).getId());
  }

  @Test
  void createNewProduct_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    when(categoryService.getCategoryById(category.getId())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productService.createNewProduct(productRequest));
  }

  @Test
  void getProductById_whenProductExists_thenReturnProduct() {
    when(productRepository.findById(productEntity.getId())).thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(productEntity)).thenReturn(product);

    assertTrue(productService.getProductById(productEntity.getId()).isPresent());
  }

  @Test
  void getProductById_whenProductDoesNotExist_thenReturnEmpty() {
    when(productRepository.findById(productEntity.getId())).thenReturn(Optional.empty());

    assertTrue(productService.getProductById(productEntity.getId()).isEmpty());
  }

  @Test
  void getAllProducts_whenProductsExist_thenReturnPagedResponse() {
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity));
    when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(productMapper.fromEntity(productEntity)).thenReturn(product);

    assertEquals(1, productService.getAllProducts(0, 10).getTotalPages());
  }

  @Test
  void getFilteredProducts_whenProductsExist_thenReturnPagedResponse() {
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity));
    when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
    when(productMapper.fromEntity(productEntity)).thenReturn(product);

    assertEquals(
        1, productService.getFilteredProducts(productFilterRequest, 0, 10).getTotalPages());
  }

  @Test
  void updateProduct_whenProductExistsAndCategoryExists_thenUpdateProduct() {
    when(productRepository.findById(productEntity.getId())).thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(productEntity)).thenReturn(product);
    when(categoryService.getCategoryById(category.getId())).thenReturn(Optional.of(category));
    ProductEntity updatedEntity =
        ProductEntityTestDataBuilder.builder()
            .name(productRequest2.getName())
            .build()
            .buildProductEntity();
    when(productRepository.save(any())).thenReturn(updatedEntity);

    assertDoesNotThrow(() -> productService.updateProduct(productEntity.getId(), productRequest2));
  }

  @Test
  void updateProduct_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(productRepository.findById(productEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> productService.updateProduct(productEntity.getId(), productRequest2));
  }

  @Test
  void updateProduct_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    when(productRepository.findById(productEntity.getId())).thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(productEntity)).thenReturn(product);
    when(categoryService.getCategoryById(category.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> productService.updateProduct(productEntity.getId(), productRequest2));
  }

  @Test
  void deleteProduct_whenProductExists_thenDeleteSuccessfully() {
    when(productRepository.existsById(productEntity.getId())).thenReturn(true);

    assertDoesNotThrow(() -> productService.deleteProduct(productEntity.getId()));
  }

  @Test
  void deleteProduct_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(productRepository.existsById(productEntity.getId())).thenReturn(false);

    assertThrows(
        NotFoundException.class, () -> productService.deleteProduct(productEntity.getId()));
  }
}
