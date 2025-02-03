package me.jangluzniewicz.webstore.order_statuses.repositories;

import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatusEntity, Long> {
  boolean existsByNameIgnoreCase(String name);
}
