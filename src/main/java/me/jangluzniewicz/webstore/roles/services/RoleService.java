package me.jangluzniewicz.webstore.roles.services;

import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.IdResponse;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
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
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RoleService implements IRole {
  private final RoleRepository roleRepository;
  private final RoleMapper roleMapper;

  public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
    this.roleRepository = roleRepository;
    this.roleMapper = roleMapper;
  }

  @Override
  @Transactional
  public IdResponse createNewRole(RoleRequest roleRequest) {
    if (roleRepository.existsByNameIgnoreCase(roleRequest.getName())) {
      throw new NotUniqueException("Role with name " + roleRequest.getName() + " already exists");
    }
    Role role = Role.builder().name(roleRequest.getName()).build();
    return new IdResponse(roleRepository.save(roleMapper.toEntity(role)).getId());
  }

  @Override
  public Optional<Role> getRoleById(Long id) {
    return roleRepository.findById(id).map(roleMapper::fromEntity);
  }

  @Override
  public PagedResponse<Role> getAllRoles(Integer page, Integer size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Role> roles = roleRepository.findAll(pageable).map(roleMapper::fromEntity);
    return new PagedResponse<>(roles.getTotalPages(), roles.toList());
  }

  @Override
  @Transactional
  public void updateRole(Long id, RoleRequest roleRequest) {
    Role role =
        getRoleById(id)
            .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
    if (roleRepository.existsByNameIgnoreCase(roleRequest.getName())
        && !role.getName().equals(roleRequest.getName())) {
      throw new NotUniqueException("Role with name " + roleRequest.getName() + " already exists");
    }
    role.setName(roleRequest.getName());
    roleRepository.save(roleMapper.toEntity(role));
  }

  @Override
  @Transactional
  public void deleteRole(Long id) {
    if (!roleRepository.existsById(id)) {
      throw new NotFoundException("Role with id " + id + " not found");
    }
    try {
      roleRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException) {
        throw new DeletionNotAllowedException(
            "Role with id " + id + " cannot be deleted due to existing relations");
      } else {
        throw e;
      }
    }
  }
}
