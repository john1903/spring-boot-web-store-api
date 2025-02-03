package me.jangluzniewicz.webstore.products.services;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.categories.mappers.CategoryMapper;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.mappers.ProductMapper;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.products.repositories.ProductRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProductService implements IProduct {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ICategory categoryService;
    private final CategoryMapper categoryMapper;

    public ProductService(ProductRepository productRepository,
                          ProductMapper productMapper, ICategory categoryService, CategoryMapper categoryMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Long createNewProduct(@NotNull ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .weight(productRequest.getWeight())
                .category(categoryService.getCategoryById(productRequest.getCategoryId())
                        .orElseThrow(() ->
                                new NotFoundException("Category with id " +
                                        productRequest.getCategoryId() + " not found")))
                .build();
        return productRepository.save(productMapper.toEntity(product)).getId();
    }

    @Override
    public Long updateProduct(@NotNull @Min(1) Long id, @NotNull ProductRequest productRequest) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));
        productEntity.setName(productRequest.getName());
        productEntity.setDescription(productRequest.getDescription());
        productEntity.setPrice(productRequest.getPrice());
        productEntity.setWeight(productRequest.getWeight());
        productEntity.setCategory(categoryService.getCategoryById(productRequest.getCategoryId())
                .map(categoryMapper::toEntity)
                .orElseThrow(() ->
                        new NotFoundException("Category with id "
                                + productRequest.getCategoryId() + " not found")));
        return productEntity.getId();
    }

    @Override
    public Optional<Product> getProductById(@NotNull @Min(1) Long id) {
        return productRepository.findById(id)
                .map(productMapper::fromEntity);
    }

    @Override
    public PagedResponse<Product> getAllProducts(@NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable).map(productMapper::fromEntity);
        return new PagedResponse<>(products.getTotalPages(), products.toList());
    }

    @Override
    public PagedResponse<Product> getFilteredProducts(@Min(1) Long categoryId, @Size(min = 1, max = 255) String name,
                                                      @DecimalMin("0.0") BigDecimal priceFrom,
                                                      @DecimalMin("0.0") BigDecimal priceTo,
                                                      @NotNull @Min(0) Integer page,
                                                      @NotNull @Min(1) Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;
        if (categoryId == null && name == null && priceFrom == null && priceTo == null) {
            return getAllProducts(page, size);
        } else if (categoryId != null && name == null && priceFrom == null && priceTo == null) {
            products = productRepository.findAllByCategoryId(categoryId, pageable)
                    .map(productMapper::fromEntity);
        } else if (categoryId == null && name != null && priceFrom == null && priceTo == null) {
            products = productRepository
                    .findAllByNameContainingIgnoreCase(name, pageable)
                    .map(productMapper::fromEntity);
        } else if (categoryId == null && name == null && priceFrom != null && priceTo != null) {
            products = productRepository
                    .findAllByPriceBetween(priceFrom, priceTo, pageable)
                    .map(productMapper::fromEntity);
        } else if (categoryId != null && name != null && priceFrom == null && priceTo == null) {
            products = productRepository
                    .findAllByCategoryIdAndNameContainingIgnoreCase(categoryId, name, pageable)
                    .map(productMapper::fromEntity);
        } else if (categoryId != null && name == null && priceFrom != null && priceTo != null) {
            products = productRepository
                    .findAllByCategoryIdAndPriceBetween(categoryId, priceFrom, priceTo, pageable)
                    .map(productMapper::fromEntity);
        } else if (categoryId == null && name != null && priceFrom != null && priceTo != null) {
            products = productRepository.findAllByNameContainingIgnoreCaseAndPriceBetween(name, priceFrom,
                    priceTo, pageable).map(productMapper::fromEntity);
        } else {
            products =
                    productRepository.findAllByCategoryIdAndNameContainingIgnoreCaseAndPriceBetween(categoryId, name,
                            priceFrom, priceTo, pageable).map(productMapper::fromEntity);
        }
        return new PagedResponse<>(products.getTotalPages(), products.toList());
    }

    @Override
    public void deleteProduct(@NotNull @Min(1) Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product with id " + id + " not found");
        }
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new DeletionNotAllowedException("Product with id " + id +
                        " cannot be deleted due to existing relations");
            } else {
                throw e;
            }
        }
    }
}
