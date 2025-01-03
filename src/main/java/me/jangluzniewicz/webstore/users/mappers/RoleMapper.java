package me.jangluzniewicz.webstore.users.mappers;

import me.jangluzniewicz.webstore.users.entities.RoleEntity;
import me.jangluzniewicz.webstore.users.models.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role fromEntity(RoleEntity entity);
    RoleEntity toEntity(Role model);
}
