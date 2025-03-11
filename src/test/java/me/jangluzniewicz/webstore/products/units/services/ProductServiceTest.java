package me.jangluzniewicz.webstore.products.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.commons.interfaces.ICsvReader;
import me.jangluzniewicz.webstore.exceptions.CsvReaderException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.mappers.ProductMapper;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.products.repositories.ProductRepository;
import me.jangluzniewicz.webstore.products.services.ProductService;
import me.jangluzniewicz.webstore.utils.testdata.categories.CategoryTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.products.ProductEntityTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.products.ProductFilterRequestTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.products.ProductRequestTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.products.ProductTestDataBuilder;
import me.jangluzniewicz.webstore.utils.units.config.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

class ProductServiceTest extends UnitTest {
  @Mock private ProductRepository productRepository;
  @Mock private ProductMapper productMapper;
  @Mock private ICategory categoryService;
  @Mock private ICsvReader<ProductRequest> csvProductRequestReader;
  @InjectMocks private ProductService productService;

  @Mock private MultipartFile file;
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
        ProductRequestTestDataBuilder.builder()
            .name("Headphones Pro")
            .build()
            .buildProductRequest();
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
  @SuppressWarnings("unchecked")
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
    when(productRepository.findById(productEntity.getId())).thenReturn(Optional.of(productEntity));

    assertDoesNotThrow(() -> productService.deleteProduct(productEntity.getId()));
  }

  @Test
  void deleteProduct_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(productRepository.findById(productEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> productService.deleteProduct(productEntity.getId()));
  }

  @Test
  void createNewProductsFromCsv_whenCsvFileIsValid_thenCreateProducts() throws IOException {
    when(file.getContentType()).thenReturn("text/csv");
    String csv =
        """
        Bicycle,Two wheels,1000,10,1
        """;
    InputStream inputStream = new ByteArrayInputStream(csv.getBytes());
    when(file.getInputStream()).thenReturn(inputStream);
    when(csvProductRequestReader.csvToModel(inputStream)).thenReturn(List.of(productRequest));
    when(categoryService.getCategoryById(productRequest.getCategoryId()))
        .thenReturn(Optional.of(category));
    when(productMapper.toEntity(any())).thenReturn(productEntity);
    when(productRepository.saveAll(any())).thenReturn(List.of(productEntity));

    assertDoesNotThrow(() -> productService.createNewProductsFromCsv(file));
  }

  @Test
  void createNewProductsFromCsv_whenCategoryNotFound_thenThrowNotFoundException()
      throws IOException {
    when(file.getContentType()).thenReturn("text/csv");
    String csv =
        """
        Bicycle,Two wheels,1000,10,1
        """;
    InputStream inputStream = new ByteArrayInputStream(csv.getBytes());
    when(file.getInputStream()).thenReturn(inputStream);
    when(csvProductRequestReader.csvToModel(inputStream)).thenReturn(List.of(productRequest));
    when(categoryService.getCategoryById(productRequest.getCategoryId()))
        .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productService.createNewProductsFromCsv(file));
  }

  @Test
  void createNewProductsFromCsv_whenFileIsNotCsv_thenThrowIllegalArgumentException() {
    when(file.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);

    assertThrows(
        IllegalArgumentException.class, () -> productService.createNewProductsFromCsv(file));
  }

  @Test
  void createNewProductsFromCsv_whenErrorWhileReadingCsv_thenThrowCsvReaderException()
      throws IOException {
    when(file.getContentType()).thenReturn("text/csv");
    when(file.getInputStream()).thenThrow(IOException.class);

    assertThrows(CsvReaderException.class, () -> productService.createNewProductsFromCsv(file));
  }
}
