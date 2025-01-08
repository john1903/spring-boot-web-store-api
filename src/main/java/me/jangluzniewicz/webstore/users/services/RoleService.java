package me.jangluzniewicz.webstore.users.services;

import jakarta.validation.Valid;
import me.jangluzniewicz.webstore.exceptions.NotFound;
import me.jangluzniewicz.webstore.exceptions.NotUnique;
import me.jangluzniewicz.webstore.users.entities.RoleEntity;
import me.jangluzniewicz.webstore.users.mappers.RoleMapper;
import me.jangluzniewicz.webstore.users.models.Role;
import me.jangluzniewicz.webstore.users.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public Long createNewRole(@Valid Role role) {
        if (roleRepository.existsByNameLike(role.getName())) {
            throw new NotUnique("Role with name " + role.getName() + " already exists");
        }
        return roleRepository.save(roleMapper.toEntity(role)).getId();
    }

    public Role getRoleById(Long id) {
        Optional<RoleEntity> optionalRoleEntity = roleRepository.findById(id);
        if (optionalRoleEntity.isEmpty()) {
            throw new NotFound("Role with id " + id + " not found");
        }
        return roleMapper.fromEntity(optionalRoleEntity.get());
    }

    public List<Role> getAllRoles() {
        return StreamSupport.stream(roleRepository.findAll().spliterator(), false)
                .map(roleMapper::fromEntity)
                .toList();
    }

    public Role updateRole(Long id, @Valid Role role) {
        Optional<RoleEntity> optionalRoleEntity = roleRepository.findById(id);
        if (optionalRoleEntity.isEmpty()) {
            throw new NotFound("Role with id " + id + " not found");
        }
        RoleEntity roleEntity = optionalRoleEntity.get();
        if (roleRepository.existsByNameLike(role.getName())) {
            throw new NotUnique("Role with name " + role.getName() + " already exists");
        }
        roleEntity.setName(role.getName());
        return roleMapper.fromEntity(roleRepository.save(roleEntity));
    }

    public void deleteRole(Long id) {
        Optional<RoleEntity> optionalRoleEntity = roleRepository.findById(id);
        if (optionalRoleEntity.isEmpty()) {
            throw new NotFound("Role with id " + id + " not found");
        }
        roleRepository.delete(optionalRoleEntity.get());
    }
}
