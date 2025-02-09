package me.jangluzniewicz.webstore.orders.repositories;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import me.jangluzniewicz.webstore.orders.controllers.OrderFilterRequest;
import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
  public static Specification<OrderEntity> filterBy(OrderFilterRequest filter) {
    return ((root, _, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (filter.getStatusId() != null) {
        predicates.add(criteriaBuilder.equal(root.get("status").get("id"), filter.getStatusId()));
      }
      if (filter.getOrderDateAfter() != null) {
        predicates.add(
            criteriaBuilder.greaterThanOrEqualTo(
                root.get("orderDate"), filter.getOrderDateAfter()));
      }
      if (filter.getOrderDateBefore() != null) {
        predicates.add(
            criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), filter.getOrderDateBefore()));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    });
  }
}
