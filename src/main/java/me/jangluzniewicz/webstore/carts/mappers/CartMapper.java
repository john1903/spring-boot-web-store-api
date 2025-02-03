package me.jangluzniewicz.webstore.carts.mappers;

import me.jangluzniewicz.webstore.carts.entities.CartEntity;
import me.jangluzniewicz.webstore.carts.models.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
  Cart fromEntity(CartEntity entity);

  CartEntity toEntity(Cart model);
}
