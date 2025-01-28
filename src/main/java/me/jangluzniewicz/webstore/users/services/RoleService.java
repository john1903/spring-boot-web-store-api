package me.jangluzniewicz.webstore.users.services;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.users.controllers.RoleRequest;
import me.jangluzniewicz.webstore.users.entities.RoleEntity;
import me.jangluzniewicz.webstore.users.interfaces.IRole;
import me.jangluzniewicz.webstore.users.mappers.RoleMapper;
import me.jangluzniewicz.webstore.users.models.Role;
import me.jangluzniewicz.webstore.users.repositories.RoleRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

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
        if (roleRepository.existsByNameLike(roleRequest.getName())) {
            throw new NotUniqueException("Role with name " + roleRequest.getName() + " already exists");
        }
        Role role = Role.builder()
                .name(roleRequest.getName())
                .build();
        return roleRepository.save(roleMapper.toEntity(role)).getId();
    }

    @Override
    public Optional<Role> getRoleById(@Min(1) Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::fromEntity);
    }

    @Override
    public List<Role> getAllRoles() {
        return StreamSupport.stream(roleRepository.findAll().spliterator(), false)
                .map(roleMapper::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public Long updateRole(@Min(1) Long id, @NotNull RoleRequest roleRequest) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
        if (roleRepository.existsByNameLike(roleRequest.getName()) &&
                !roleEntity.getName().equals(roleRequest.getName())) {
            throw new NotUniqueException("Role with name " + roleRequest.getName() + " already exists");
        }
        roleEntity.setName(roleRequest.getName());
        return roleEntity.getId();
    }

    @Override
    @Transactional
    public void deleteRole(@Min(1) Long id) {
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