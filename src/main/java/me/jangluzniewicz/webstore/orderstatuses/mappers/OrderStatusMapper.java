package me.jangluzniewicz.webstore.orderstatuses.mappers;

import me.jangluzniewicz.webstore.orderstatuses.entities.OrderStatusEntity;
import me.jangluzniewicz.webstore.orderstatuses.models.OrderStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderStatusMapper {
  OrderStatus fromEntity(OrderStatusEntity entity);

  OrderStatusEntity toEntity(OrderStatus model);
}
