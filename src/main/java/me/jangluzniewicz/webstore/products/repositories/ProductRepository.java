package me.jangluzniewicz.webstore.products.repositories;

import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<ProductEntity> findAllByCategoryId(Long categoryId, Pageable pageable);
    Page<ProductEntity> findAllByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name, Pageable pageable);
    Page<ProductEntity> findAllByPriceBetween(BigDecimal priceFrom, BigDecimal priceTo, Pageable pageable);
    Page<ProductEntity> findAllByCategoryIdAndPriceBetween(Long categoryId, BigDecimal priceFrom,
                                                       BigDecimal priceTo, Pageable pageable);
    Page<ProductEntity> findAllByNameContainingIgnoreCaseAndPriceBetween(String name, BigDecimal priceFrom,
                                                                   BigDecimal priceTo, Pageable pageable);
    Page<ProductEntity> findAllByCategoryIdAndNameContainingIgnoreCaseAndPriceBetween(Long categoryId, String name,
                                                                                      BigDecimal priceFrom,
                                                                                      BigDecimal priceTo,
                                                                                      Pageable pageable);
}
