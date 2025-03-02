package me.jangluzniewicz.webstore.products.interfaces;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.products.models.Product;
import org.springframework.web.multipart.MultipartFile;

/** Interface for managing products. */
public interface IProduct {

  /**
   * Creates a new product.
   *
   * @param productRequest the request object containing the details of the product to be created;
   *     must not be null.
   * @return an {@link IdResponse} containing the ID of the newly created product.
   */
  IdResponse createNewProduct(@NotNull ProductRequest productRequest);

  /**
   * Creates new products from a CSV file.
   *
   * @param file the CSV file containing the product details; must not be null.
   */
  void createNewProductsFromCsv(@NotNull MultipartFile file);

  /**
   * Updates an existing product.
   *
   * @param id the ID of the product to be updated; must be a positive number.
   * @param productRequest the request object containing the updated details of the product; must
   *     not be null.
   */
  void updateProduct(@NotNull @Min(1) Long id, @NotNull ProductRequest productRequest);

  /**
   * Retrieves a product by its ID.
   *
   * @param id the ID of the product to be retrieved; must be a positive number.
   * @return an {@link Optional} containing the {@link Product} if found, or empty if not found.
   */
  Optional<Product> getProductById(@NotNull @Min(1) Long id);

  /**
   * Retrieves filtered products with pagination.
   *
   * @param filter the filter criteria for retrieving products; must not be null.
   * @param page the page number to retrieve; must be a non-negative number.
   * @param size the number of products per page; must be a positive number.
   * @return a {@link PagedResponse} containing the paginated list of filtered products.
   */
  PagedResponse<Product> getFilteredProducts(
      @NotNull ProductFilterRequest filter,
      @NotNull @Min(0) Integer page,
      @NotNull @Min(1) Integer size);

  /**
   * Deletes a product by its ID.
   *
   * @param id the ID of the product to be deleted; must be a positive number.
   */
  void deleteProduct(@NotNull @Min(1) Long id);
}
