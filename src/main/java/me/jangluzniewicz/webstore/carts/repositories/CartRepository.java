package me.jangluzniewicz.webstore.carts.repositories;

import me.jangluzniewicz.webstore.carts.entities.CartEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartRepository extends CrudRepository<CartEntity, Long> {
    Optional<CartEntity> findByCustomerId(Long customerId);
}
