package me.jangluzniewicz.webstore.carts.mappers;

import me.jangluzniewicz.webstore.carts.entities.CartItemEntity;
import me.jangluzniewicz.webstore.carts.models.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItem fromEntity(CartItemEntity entity);
    CartItemEntity toEntity(CartItem model);
}
