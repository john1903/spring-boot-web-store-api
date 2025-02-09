package me.jangluzniewicz.webstore.products.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.mappers.CategoryMapper;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.mappers.ProductMapper;
import me.jangluzniewicz.webstore.products.models.Product;
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

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  @Mock private ProductRepository productRepository;
  @Mock private ProductMapper productMapper;
  @Mock private ICategory categoryService;
  @Mock private CategoryMapper categoryMapper;
  @InjectMocks private ProductService productService;
  private final String PRODUCT_NAME = "Bicycle";
  private final String PRODUCT_DESCRIPTION = "Mountain bike";
  private final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1000.0);
  private final BigDecimal PRODUCT_WEIGHT = BigDecimal.valueOf(10.0);
  private CategoryEntity categoryEntity;
  private Category category;

  @BeforeEach
  void setUp() {
    category = new Category(1L, "Bikes");
    categoryEntity = new CategoryEntity(1L, "Bikes");
  }

  @Test
  public void createNewProduct_whenCategoryExists_thenReturnProductId() {
    when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
    when(productMapper.toEntity(any()))
        .thenReturn(
            ProductEntity.builder()
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .weight(PRODUCT_WEIGHT)
                .category(categoryEntity)
                .build());
    when(productRepository.save(any()))
        .thenReturn(
            ProductEntity.builder()
                .id(1L)
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .weight(PRODUCT_WEIGHT)
                .category(categoryEntity)
                .build());

    assertEquals(
        1L,
        productService.createNewProduct(
            new ProductRequest(
                PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_WEIGHT, 1L)));
  }

  @Test
  public void createNewProduct_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () ->
            productService.createNewProduct(
                new ProductRequest(
                    PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_WEIGHT, 1L)));
  }

  @Test
  public void getProductById_whenProductExists_thenReturnProduct() {
    ProductEntity productEntity =
        new ProductEntity(
            1L, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_WEIGHT, categoryEntity);

    when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_WEIGHT, category));

    Optional<Product> product = productService.getProductById(1L);

    assertTrue(product.isPresent());
    assertEquals(1L, product.get().getId());
    assertEquals(PRODUCT_NAME, product.get().getName());
    assertEquals(PRODUCT_DESCRIPTION, product.get().getDescription());
    assertEquals(PRODUCT_PRICE, product.get().getPrice());
    assertEquals(PRODUCT_WEIGHT, product.get().getWeight());
    assertEquals(category, product.get().getCategory());
  }

  @Test
  public void getProductById_whenProductDoesNotExist_thenReturnEmpty() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<Product> product = productService.getProductById(1L);

    assertTrue(product.isEmpty());
  }

  @Test
  public void getAllProducts_whenProductsExist_thenReturnPagedResponse() {
    ProductEntity productEntity =
        new ProductEntity(
            1L, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_WEIGHT, categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAll(pageable)).thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_WEIGHT, category));

    PagedResponse<Product> products = productService.getAllProducts(0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals(PRODUCT_NAME, products.getContent().getFirst().getName());
    assertEquals(PRODUCT_DESCRIPTION, products.getContent().getFirst().getDescription());
    assertEquals(PRODUCT_PRICE, products.getContent().getFirst().getPrice());
    assertEquals(PRODUCT_WEIGHT, products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void updateProduct_whenProductExistsAndCategoryExists_thenReturnProductId() {
    ProductRequest productRequest =
        new ProductRequest(
            "Bicycle XXL", PRODUCT_DESCRIPTION, BigDecimal.valueOf(1200.0), PRODUCT_WEIGHT, 1L);
    ProductEntity savedEntity =
        new ProductEntity(
            1L, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_WEIGHT, categoryEntity);

    when(productRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
    when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
    when(categoryMapper.toEntity(category)).thenReturn(categoryEntity);

    Long productId = productService.updateProduct(1L, productRequest);

    assertEquals(1L, productId);
  }

  @Test
  public void updateProduct_whenProductDoesNotExist_thenThrowNotFoundException() {
    ProductRequest productRequest =
        new ProductRequest(
            "Bicycle XXL", PRODUCT_DESCRIPTION, BigDecimal.valueOf(1200.0), PRODUCT_WEIGHT, 1L);

    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productRequest));
  }

  @Test
  public void updateProduct_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    ProductRequest productRequest =
        new ProductRequest(
            "Bicycle XXL", PRODUCT_DESCRIPTION, BigDecimal.valueOf(1200.0), PRODUCT_WEIGHT, 1L);
    ProductEntity savedEntity =
        new ProductEntity(
            1L, PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, PRODUCT_WEIGHT, categoryEntity);

    when(productRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
    when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productRequest));
  }

  @Test
  public void deleteProduct_whenProductExists_thenDeleteSuccessfully() {
    when(productRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> productService.deleteProduct(1L));
  }

  @Test
  public void deleteProduct_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(productRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
  }

  @Test
  public void deleteProduct_whenProductHasDependencies_thenThrowDeletionNotAllowedException() {
    when(productRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(productRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> productService.deleteProduct(1L));
  }

  @Test
  public void deleteProduct_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(productRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(productRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> productService.deleteProduct(1L));
  }
}
