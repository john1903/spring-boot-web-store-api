package me.jangluzniewicz.webstore.orders.mappers;

import me.jangluzniewicz.webstore.orders.entities.OrderEntity;
import me.jangluzniewicz.webstore.orders.models.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order fromEntity(OrderEntity entity);
    OrderEntity toEntity(Order model);
}
