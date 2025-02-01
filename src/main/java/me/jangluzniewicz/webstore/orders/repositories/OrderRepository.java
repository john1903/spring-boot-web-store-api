package me.jangluzniewicz.webstore.orders.repositories;

import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findAllByStatusIdOrderByOrderDateAscIdAsc(Long statusId, Pageable pageable);
    Page<OrderEntity> findAllByCustomerIdOrderByOrderDateAscIdAsc(Long customerId, Pageable pageable);
    Page<OrderEntity> findAllByOrderDateBetweenOrderByOrderDateAscIdAsc(LocalDateTime orderDateAfter,
                                                                        LocalDateTime orderDateBefore, Pageable pageable);
    Page<OrderEntity> findAllByStatusIdAndOrderDateBetweenOrderByOrderDateAscIdAsc(Long statusId,
                                                                                   LocalDateTime orderDateAfter,
                                                                                   LocalDateTime orderDateBefore,
                                                                                   Pageable pageable);
    boolean existsByRatingIsNotNullAndId(Long id);
}
