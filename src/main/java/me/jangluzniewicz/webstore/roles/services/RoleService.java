package me.jangluzniewicz.webstore.roles.services;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.interfaces.IRole;
import me.jangluzniewicz.webstore.roles.mappers.RoleMapper;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.roles.repositories.RoleRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RoleService implements IRole {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public Long createNewRole(@NotNull RoleRequest roleRequest) {
        if (roleRepository.existsByNameIgnoreCase(roleRequest.getName())) {
            throw new NotUniqueException("Role with name " + roleRequest.getName() + " already exists");
        }
        Role role = Role.builder()
                .name(roleRequest.getName())
                .build();
        return roleRepository.save(roleMapper.toEntity(role)).getId();
    }

    @Override
    public Optional<Role> getRoleById(@NotNull @Min(1) Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::fromEntity);
    }

    @Override
    public PagedResponse<Role> getAllRoles(@NotNull @Min(0) Integer page, @NotNull @Min(1) Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> roles = roleRepository.findAll(pageable).map(roleMapper::fromEntity);
        return new PagedResponse<>(roles.getTotalPages(), roles.toList());
    }

    @Override
    @Transactional
    public Long updateRole(@NotNull @Min(1) Long id, @NotNull RoleRequest roleRequest) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
        if (roleRepository.existsByNameIgnoreCase(roleRequest.getName()) &&
                !roleEntity.getName().equals(roleRequest.getName())) {
            throw new NotUniqueException("Role with name " + roleRequest.getName() + " already exists");
        }
        roleEntity.setName(roleRequest.getName());
        return roleEntity.getId();
    }

    @Override
    @Transactional
    public void deleteRole(@NotNull @Min(1) Long id) {
        if (!roleRepository.existsById(id)) {
            throw new NotFoundException("Role with id " + id + " not found");
        }
        try {
            roleRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new DeletionNotAllowedException("Role with id " + id +
                        " cannot be deleted due to existing relations");
            }
        }
    }
}