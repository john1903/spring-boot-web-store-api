package me.jangluzniewicz.webstore.orders.repositories;

import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.domain.Pageable;
import java.sql.Timestamp;
import java.util.List;

public interface OrderRepository extends PagingAndSortingRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByStatusIdOrderByOrderDateAscIdAsc(Long statusId, Pageable pageable);
    List<OrderEntity> findAllByCustomerIdOrderByOrderDateAscIdAsc(Long customerId, Pageable pageable);
    List<OrderEntity> findAllByOrderDateBetweenOrderByOrderDateAscIdAsc(Timestamp orderDateAfter,
                                                                        Timestamp orderDateBefore, Pageable pageable);
    List<OrderEntity> findAllByStatusIdAndOrderDateBetweenOrderByOrderDateAscIdAsc(Long statusId,
                                                                                   Timestamp orderDateAfter,
                                                                                   Timestamp orderDateBefore,
                                                                                   Pageable pageable);
    boolean existsByRatingIsNotNullAndId(Long id);
}
