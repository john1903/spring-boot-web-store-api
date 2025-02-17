package me.jangluzniewicz.webstore.orderstatuses.repositories;

import me.jangluzniewicz.webstore.orderstatuses.entities.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatusEntity, Long> {
  boolean existsByNameIgnoreCase(String name);
}
