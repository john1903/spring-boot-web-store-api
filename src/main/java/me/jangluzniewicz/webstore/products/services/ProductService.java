package me.jangluzniewicz.webstore.products.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.mappers.ProductMapper;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.products.repositories.ProductRepository;
import me.jangluzniewicz.webstore.products.repositories.ProductSpecification;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProduct {
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;
  private final ICategory categoryService;

  public ProductService(
      ProductRepository productRepository, ProductMapper productMapper, ICategory categoryService) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
    this.categoryService = categoryService;
  }

  @Override
  @Transactional
  public IdResponse createNewProduct(@NotNull ProductRequest productRequest) {
    Product product =
        Product.builder()
            .name(productRequest.getName())
            .description(productRequest.getDescription())
            .price(productRequest.getPrice())
            .weight(productRequest.getWeight())
            .category(
                categoryService
                    .getCategoryById(productRequest.getCategoryId())
                    .orElseThrow(
                        () ->
                            new NotFoundException(
                                "Category with id "
                                    + productRequest.getCategoryId()
                                    + " not found")))
            .build();
    return new IdResponse(productRepository.save(productMapper.toEntity(product)).getId());
  }

  @Override
  @Transactional
  public void updateProduct(@NotNull @Min(1) Long id, @NotNull ProductRequest productRequest) {
    Product product =
        getProductById(id)
            .orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));
    product.setName(productRequest.getName());
    product.setDescription(productRequest.getDescription());
    product.setPrice(productRequest.getPrice());
    product.setWeight(productRequest.getWeight());
    product.setCategory(
        categoryService
            .getCategoryById(productRequest.getCategoryId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Category with id " + productRequest.getCategoryId() + " not found")));
    productRepository.save(productMapper.toEntity(product));
  }

  @Override
  public Optional<Product> getProductById(@NotNull @Min(1) Long id) {
    return productRepository.findById(id).map(productMapper::fromEntity);
  }

  @Override
  public PagedResponse<Product> getAllProducts(
      @NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Product> products = productRepository.findAll(pageable).map(productMapper::fromEntity);
    return new PagedResponse<>(products.getTotalPages(), products.toList());
  }

  @Override
  public PagedResponse<Product> getFilteredProducts(
      @NotNull ProductFilterRequest filter,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Specification<ProductEntity> specification = ProductSpecification.filterBy(filter);
    Page<Product> products =
        productRepository.findAll(specification, pageable).map(productMapper::fromEntity);
    return new PagedResponse<>(products.getTotalPages(), products.toList());
  }

  @Override
  @Transactional
  public void deleteProduct(@NotNull @Min(1) Long id) {
    if (!productRepository.existsById(id)) {
      throw new NotFoundException("Product with id " + id + " not found");
    }
    try {
      productRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException) {
        throw new DeletionNotAllowedException(
            "Product with id " + id + " cannot be deleted due to existing relations");
      } else {
        throw e;
      }
    }
  }
}
