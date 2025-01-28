package me.jangluzniewicz.webstore.products.repositories;

import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    List<ProductEntity> findAllByCategoryId(Long categoryId, Pageable pageable);
    List<ProductEntity> findAllByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name, Pageable pageable);
    List<ProductEntity> findAllByPriceBetween(BigDecimal priceFrom, BigDecimal priceTo, Pageable pageable);
    List<ProductEntity> findAllByCategoryIdAndPriceBetween(Long categoryId, BigDecimal priceFrom,
                                                           BigDecimal priceTo, Pageable pageable);
    List<ProductEntity> findAllByCategoryIdAndNameContainingIgnoreCaseAndPriceBetween(Long categoryId, String name,
                                                                                      BigDecimal priceFrom,
                                                                                      BigDecimal priceTo,
                                                                                      Pageable pageable);
}
