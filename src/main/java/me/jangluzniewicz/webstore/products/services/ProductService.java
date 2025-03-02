package me.jangluzniewicz.webstore.products.services;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.aws.interfaces.IAwsS3;
import me.jangluzniewicz.webstore.categories.interfaces.ICategory;
import me.jangluzniewicz.webstore.commons.interfaces.ICsvReader;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.CsvReaderException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.mappers.ProductMapper;
import me.jangluzniewicz.webstore.products.models.Product;
import me.jangluzniewicz.webstore.products.repositories.ProductRepository;
import me.jangluzniewicz.webstore.products.repositories.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Service
@Validated
public class ProductService implements IProduct {
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;
  private final ICategory categoryService;
  private final ICsvReader<ProductRequest> csvReader;
  private final IAwsS3 awsS3;

  public ProductService(
      ProductRepository productRepository,
      ProductMapper productMapper,
      ICategory categoryService,
      ICsvReader<ProductRequest> csvReader,
      IAwsS3 awsS3) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
    this.categoryService = categoryService;
    this.csvReader = csvReader;
    this.awsS3 = awsS3;
  }

  @Override
  @Transactional
  public IdResponse createNewProduct(ProductRequest productRequest) {
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
  public void createNewProductsFromCsv(MultipartFile file) {
    final String TYPE = "text/csv";
    if (!TYPE.equals(file.getContentType())) {
      throw new IllegalArgumentException("File must be of type text/csv");
    }
    List<ProductRequest> productRequests;
    try {
      productRequests = csvReader.csvToModel(file.getInputStream());
    } catch (IOException e) {
      throw new CsvReaderException("Error while reading CSV file");
    }
    List<ProductEntity> productEntities =
        productRequests.stream()
            .map(
                productRequest ->
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
                        .build())
            .map(productMapper::toEntity)
            .toList();
    productRepository.saveAll(productEntities);
  }

  @Override
  @Transactional
  public void updateProduct(Long id, ProductRequest productRequest) {
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
  public Optional<Product> getProductById(Long id) {
    return productRepository
        .findById(id)
        .map(
            productEntity -> {
              var product = productMapper.fromEntity(productEntity);
              product.setImageUrl(
                  product.getImageUri() != null && !product.getImageUri().isEmpty()
                      ? awsS3.getSignedUrl(product.getImageUri())
                      : "");
              return product;
            });
  }

  @Override
  public PagedResponse<Product> getFilteredProducts(
      ProductFilterRequest filter, Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Specification<ProductEntity> specification = ProductSpecification.filterBy(filter);
    Page<Product> products =
        productRepository
            .findAll(specification, pageable)
            .map(
                productEntity -> {
                  var product = productMapper.fromEntity(productEntity);
                  product.setImageUrl(
                      product.getImageUri() != null && !product.getImageUri().isEmpty()
                          ? awsS3.getSignedUrl(product.getImageUri())
                          : "");
                  return product;
                });
    return new PagedResponse<>(products.getTotalPages(), products.toList());
  }

  @Override
  @Transactional
  public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new NotFoundException("Product with id " + id + " not found");
    }
    productRepository.deleteById(id);
  }
}
