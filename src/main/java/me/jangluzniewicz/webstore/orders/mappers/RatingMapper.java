package me.jangluzniewicz.webstore.orders.mappers;

import me.jangluzniewicz.webstore.orders.entities.RatingEntity;
import me.jangluzniewicz.webstore.orders.models.Rating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper {
  Rating fromEntity(RatingEntity entity);

  RatingEntity toEntity(Rating model);
}
