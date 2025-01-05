package me.jangluzniewicz.webstore.order_statuses.repositories;

import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderStatusRepository extends CrudRepository<OrderStatusEntity, Long> {
    boolean existsByName(String name);
}
