package me.jangluzniewicz.webstore.order_statuses.mappers;

import me.jangluzniewicz.webstore.order_statuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.order_statuses.models.OrderStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderStatusMapper {
  OrderStatus fromEntity(OrderStatusEntity entity);

  OrderStatusEntity toEntity(OrderStatus model);
}
