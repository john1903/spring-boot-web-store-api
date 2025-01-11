package me.jangluzniewicz.webstore.users.services;

import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.IdViolationException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.users.entities.RoleEntity;
import me.jangluzniewicz.webstore.users.mappers.RoleMapper;
import me.jangluzniewicz.webstore.users.models.Role;
import me.jangluzniewicz.webstore.users.repositories.RoleRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Transactional
    public Long createNewRole(Role role) {
        if (roleRepository.existsById(role.getId())) {
            throw new IdViolationException("Role with id " + role.getId() + " already exists");
        }
        if (roleRepository.existsByNameLike(role.getName())) {
            throw new NotUniqueException("Role with name " + role.getName() + " already exists");
        }
        return roleRepository.save(roleMapper.toEntity(role)).getId();
    }

    public Role getRoleById(Long id) {
        return roleMapper.fromEntity(roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found")));
    }

    public List<Role> getAllRoles() {
        return StreamSupport.stream(roleRepository.findAll().spliterator(), false)
                .map(roleMapper::fromEntity)
                .toList();
    }

    @Transactional
    public Role updateRole(Long id, Role role) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
        if (roleRepository.existsByNameLike(role.getName()) && !roleEntity.getName().equals(role.getName())) {
            throw new NotUniqueException("Role with name " + role.getName() + " already exists");
        }
        roleEntity.setName(role.getName());
        return roleMapper.fromEntity(roleRepository.save(roleEntity));
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new NotFoundException("Role with id " + id + " not found");
        }
        try {
            roleRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new DeletionNotAllowedException("Role with id " + id + " cannot be deleted due to existing relations");
            }
        }
    }
}