package me.jangluzniewicz.webstore.orders.repositories;

import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository
    extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
  Page<OrderEntity> findAllByCustomerIdOrderByOrderDateAscIdAsc(Long customerId, Pageable pageable);

  boolean existsByRatingIsNotNullAndId(Long id);
}
