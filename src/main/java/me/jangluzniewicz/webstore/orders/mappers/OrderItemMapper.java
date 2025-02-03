package me.jangluzniewicz.webstore.orders.mappers;

import me.jangluzniewicz.webstore.orders.entities.OrderItemEntity;
import me.jangluzniewicz.webstore.orders.models.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
  OrderItem fromEntity(OrderItemEntity entity);

  OrderItemEntity toEntity(OrderItem model);
}
