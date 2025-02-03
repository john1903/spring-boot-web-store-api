package me.jangluzniewicz.webstore.products.mappers;

import me.jangluzniewicz.webstore.products.entities.ProductEntity;
import me.jangluzniewicz.webstore.products.models.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  Product fromEntity(ProductEntity entity);

  ProductEntity toEntity(Product model);
}
