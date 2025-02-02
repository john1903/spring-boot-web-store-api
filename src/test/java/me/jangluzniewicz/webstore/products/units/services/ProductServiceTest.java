package me.jangluzniewicz.webstore.products.units.services;

import me.jangluzniewicz.webstore.categories.entities.CategoryEntity;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.models.Category;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ICategory categoryService;
    @InjectMocks
    private ProductService productService;
    private CategoryEntity categoryEntity;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Bikes");
        categoryEntity = new CategoryEntity(1L, "Bikes");
    }

    @Test
    public void shouldCreateNewProductAndReturnProductId() {
        ProductRequest productRequest = new ProductRequest("Bicycle", "Mountain bike",
                BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), 1L);
        ProductEntity savedEntity = new ProductEntity(1L, "Bicycle", "Mountain bike",
                BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), categoryEntity);

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(any())).thenReturn(new ProductEntity(null, "Bicycle",
                "Mountain bike", BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), categoryEntity));
        when(productRepository.save(any())).thenReturn(savedEntity);

        Long productId = productService.createNewProduct(productRequest);

        assertEquals(1L, productId);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenCategoryDoesNotExist() {
        ProductRequest productRequest = new ProductRequest("Bicycle", "Mountain bike",
                BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), 1L);

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.createNewProduct(productRequest));
    }

    @Test
    public void shouldReturnProductWhenGettingProductById() {
        ProductEntity productEntity = new ProductEntity(1L, "Bicycle", "Mountain bike",
                BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), categoryEntity);

        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        when(productMapper.fromEntity(productEntity)).thenReturn(new Product(1L, "Bicycle", "Mountain bike",
                BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), category));

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
    public void shouldReturnEmptyWhenProductNotFoundById() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Product> product = productService.getProductById(1L);

        assertTrue(product.isEmpty());
    }

    @Test
    public void shouldReturnPagedResponseWhenGettingAllProducts() {
        ProductEntity productEntity = new ProductEntity(1L, "Bicycle", "Mountain bike",
                BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), categoryEntity);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductEntity> page = new PageImpl<>(List.of(productEntity), pageable, 1);

        when(productRepository.findAll(pageable)).thenReturn(page);
        when(productMapper.fromEntity(productEntity)).thenReturn(new Product(1L, "Bicycle",
                "Mountain bike", BigDecimal.valueOf(1000.0), BigDecimal.valueOf(10.0), category));

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
}