package me.jangluzniewicz.webstore.products.repositories;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import me.jangluzniewicz.webstore.products.controllers.ProductFilterRequest;
import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
  public static Specification<ProductEntity> filterBy(ProductFilterRequest filter) {
    return ((root, _, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (filter.getCategoryId() != null) {
        predicates.add(
            criteriaBuilder.equal(root.get("category").get("id"), filter.getCategoryId()));
      }
      if (filter.getName() != null) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + filter.getName().toLowerCase() + "%"));
      }
      if (filter.getPriceFrom() != null) {
        predicates.add(
            criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getPriceFrom()));
      }
      if (filter.getPriceTo() != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getPriceTo()));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    });
  }
}
