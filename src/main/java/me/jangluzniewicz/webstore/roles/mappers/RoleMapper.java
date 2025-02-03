package me.jangluzniewicz.webstore.roles.mappers;

import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.models.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
  Role fromEntity(RoleEntity entity);

  RoleEntity toEntity(Role model);
}
