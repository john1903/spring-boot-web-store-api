package me.jangluzniewicz.webstore.users.mappers;

import me.jangluzniewicz.webstore.users.entities.UserEntity;
import me.jangluzniewicz.webstore.users.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User fromEntity(UserEntity entity);

  UserEntity toEntity(User model);
}
