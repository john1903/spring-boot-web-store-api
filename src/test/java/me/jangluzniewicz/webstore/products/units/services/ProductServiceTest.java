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
  private CategoryEntity categoryEntity;
  private Category category;

  @BeforeEach
  void setUp() {
    category = new Category(1L, "Bikes");
    categoryEntity = new CategoryEntity(1L, "Bikes");
  }

  @Test
  public void createNewProduct_whenCategoryExists_thenReturnProductId() {
    ProductRequest productRequest =
        new ProductRequest(
            "Bicycle", "Mountain bike", BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), 1L);
    ProductEntity savedEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);

    when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
    when(productMapper.toEntity(any()))
        .thenReturn(
            new ProductEntity(
                null,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                categoryEntity));
    when(productRepository.save(any())).thenReturn(savedEntity);

    Long productId = productService.createNewProduct(productRequest);

    assertEquals(1L, productId);
  }

  @Test
  public void createNewProduct_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    ProductRequest productRequest =
        new ProductRequest(
            "Bicycle", "Mountain bike", BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), 1L);

    when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productService.createNewProduct(productRequest));
  }

  @Test
  public void getProductById_whenProductExists_thenReturnProduct() {
    ProductEntity productEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);

    when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    Optional<Product> product = productService.getProductById(1L);

    assertTrue(product.isPresent());
    assertEquals(1L, product.get().getId());
    assertEquals("Bicycle", product.get().getName());
    assertEquals("Mountain bike", product.get().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), product.get().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), product.get().getWeight());
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
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAll(pageable)).thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    PagedResponse<Product> products = productService.getAllProducts(0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals("Bicycle", products.getContent().getFirst().getName());
    assertEquals("Mountain bike", products.getContent().getFirst().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), products.getContent().getFirst().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void updateProduct_whenProductExistsAndCategoryExists_thenReturnProductId() {
    ProductRequest productRequest =
        new ProductRequest(
            "Bicycle XXL",
            "Mountain bike",
            BigDecimal.valueOf(1200.0),
            BigDecimal.valueOf(10.0),
            1L);
    ProductEntity savedEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);

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
            "Bicycle XXL",
            "Mountain bike",
            BigDecimal.valueOf(1200.0),
            BigDecimal.valueOf(10.0),
            1L);

    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productRequest));
  }

  @Test
  public void updateProduct_whenCategoryDoesNotExist_thenThrowNotFoundException() {
    ProductRequest productRequest =
        new ProductRequest(
            "Bicycle XXL",
            "Mountain bike",
            BigDecimal.valueOf(1200.0),
            BigDecimal.valueOf(10.0),
            1L);
    ProductEntity savedEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);

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

  @Test
  public void getFilteredProducts_whenNoFiltersApplied_thenReturnPagedResponse() {
    ProductEntity productEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAll(pageable)).thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    PagedResponse<Product> products =
        productService.getFilteredProducts(null, null, null, null, 0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals("Bicycle", products.getContent().getFirst().getName());
    assertEquals("Mountain bike", products.getContent().getFirst().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), products.getContent().getFirst().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void getFilteredProducts_whenCategoryIdFilterApplied_thenReturnPagedResponse() {
    ProductEntity productEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAllByCategoryId(1L, pageable)).thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    PagedResponse<Product> products =
        productService.getFilteredProducts(1L, null, null, null, 0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals("Bicycle", products.getContent().getFirst().getName());
    assertEquals("Mountain bike", products.getContent().getFirst().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), products.getContent().getFirst().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void getFilteredProducts_whenNameFilterApplied_thenReturnPagedResponse() {
    ProductEntity productEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAllByNameContainingIgnoreCase("Bicycle", pageable)).thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    PagedResponse<Product> products =
        productService.getFilteredProducts(null, "Bicycle", null, null, 0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals("Bicycle", products.getContent().getFirst().getName());
    assertEquals("Mountain bike", products.getContent().getFirst().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), products.getContent().getFirst().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void getFilteredProducts_whenPriceRangeFilterApplied_thenReturnPagedResponse() {
    ProductEntity productEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAllByPriceBetween(
            BigDecimal.valueOf(1000.0), BigDecimal.valueOf(2000.0), pageable))
        .thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    PagedResponse<Product> products =
        productService.getFilteredProducts(
            null, null, BigDecimal.valueOf(1000.0), BigDecimal.valueOf(2000.0), 0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals("Bicycle", products.getContent().getFirst().getName());
    assertEquals("Mountain bike", products.getContent().getFirst().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), products.getContent().getFirst().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void getFilteredProducts_whenCategoryIdAndNameFiltersApplied_thenReturnPagedResponse() {
    ProductEntity productEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAllByCategoryIdAndNameContainingIgnoreCase(1L, "Bicycle", pageable))
        .thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    PagedResponse<Product> products =
        productService.getFilteredProducts(1L, "Bicycle", null, null, 0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals("Bicycle", products.getContent().getFirst().getName());
    assertEquals("Mountain bike", products.getContent().getFirst().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), products.getContent().getFirst().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void
      getFilteredProducts_whenCategoryIdAndPriceRangeFiltersApplied_thenReturnPagedResponse() {
    ProductEntity productEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAllByCategoryIdAndPriceBetween(
            1L, BigDecimal.valueOf(1000.0), BigDecimal.valueOf(2000.0), pageable))
        .thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    PagedResponse<Product> products =
        productService.getFilteredProducts(
            1L, null, BigDecimal.valueOf(1000.0), BigDecimal.valueOf(2000.0), 0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals("Bicycle", products.getContent().getFirst().getName());
    assertEquals("Mountain bike", products.getContent().getFirst().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), products.getContent().getFirst().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void getFilteredProducts_whenNameAndPriceRangeFiltersApplied_thenReturnPagedResponse() {
    ProductEntity productEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAllByNameContainingIgnoreCaseAndPriceBetween(
            "Bicycle", BigDecimal.valueOf(1000.0), BigDecimal.valueOf(2000.0), pageable))
        .thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    PagedResponse<Product> products =
        productService.getFilteredProducts(
            null, "Bicycle", BigDecimal.valueOf(1000.0), BigDecimal.valueOf(2000.0), 0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals("Bicycle", products.getContent().getFirst().getName());
    assertEquals("Mountain bike", products.getContent().getFirst().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), products.getContent().getFirst().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void getFilteredProducts_whenAllFiltersApplied_thenReturnPagedResponse() {
    ProductEntity productEntity =
        new ProductEntity(
            1L,
            "Bicycle",
            "Mountain bike",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(10.0),
            categoryEntity);
    Pageable pageable = PageRequest.of(0, 10);
    Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAllByCategoryIdAndNameContainingIgnoreCaseAndPriceBetween(
            1L, "Bicycle", BigDecimal.valueOf(1000.0), BigDecimal.valueOf(2000.0), pageable))
        .thenReturn(page);
    when(productMapper.fromEntity(productEntity))
        .thenReturn(
            new Product(
                1L,
                "Bicycle",
                "Mountain bike",
                BigDecimal.valueOf(1000.0),
                BigDecimal.valueOf(10.0),
                category));

    PagedResponse<Product> products =
        productService.getFilteredProducts(
            1L, "Bicycle", BigDecimal.valueOf(1000.0), BigDecimal.valueOf(2000.0), 0, 10);

    assertEquals(1, products.getTotalPages());
    assertEquals(1, products.getContent().size());
    assertEquals(1L, products.getContent().getFirst().getId());
    assertEquals("Bicycle", products.getContent().getFirst().getName());
    assertEquals("Mountain bike", products.getContent().getFirst().getDescription());
    assertEquals(BigDecimal.valueOf(1000.0), products.getContent().getFirst().getPrice());
    assertEquals(BigDecimal.valueOf(10.0), products.getContent().getFirst().getWeight());
    assertEquals(category, products.getContent().getFirst().getCategory());
  }

  @Test
  public void getFilteredProducts_whenPriceToIsNull_thenThrowIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            productService.getFilteredProducts(
                null, null, BigDecimal.valueOf(1000.0), null, 0, 10));
  }

  @Test
  public void getFilteredProducts_whenPriceFromIsNull_thenThrowIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            productService.getFilteredProducts(
                null, null, null, BigDecimal.valueOf(2000.0), 0, 10));
  }

  @Test
  public void
      getFilteredProducts_whenPriceFromGreaterThanPriceTo_thenThrowIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            productService.getFilteredProducts(
                null, null, BigDecimal.valueOf(2000.0), BigDecimal.valueOf(1000.0), 0, 10));
  }
}
