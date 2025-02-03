package me.jangluzniewicz.webstore.carts.repositories;

import java.util.Optional;
import me.jangluzniewicz.webstore.carts.entities.CartEntity;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<CartEntity, Long> {
  Optional<CartEntity> findByCustomerId(Long customerId);
}
