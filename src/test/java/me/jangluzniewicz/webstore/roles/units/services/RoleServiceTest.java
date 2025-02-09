package me.jangluzniewicz.webstore.roles.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.mappers.RoleMapper;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.roles.repositories.RoleRepository;
import me.jangluzniewicz.webstore.roles.services.RoleService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
  @Mock private RoleRepository roleRepository;
  @Mock private RoleMapper roleMapper;
  @InjectMocks private RoleService roleService;

  private final String ROLE_NAME = "ADMIN";

  private RoleEntity createRoleEntity(Long id, String roleName) {
    return RoleEntity.builder().id(id).name(roleName).build();
  }

  private Role createRole(Long id, String roleName) {
    return Role.builder().id(id).name(roleName).build();
  }

  @Test
  void createNewRole_whenRoleDoesNotExist_thenReturnRoleId() {
    when(roleRepository.existsByNameIgnoreCase(ROLE_NAME)).thenReturn(false);
    when(roleRepository.save(any())).thenReturn(createRoleEntity(1L, ROLE_NAME));

    assertEquals(1L, roleService.createNewRole(new RoleRequest(ROLE_NAME)));
  }

  @Test
  void createNewRole_whenRoleAlreadyExists_thenThrowNotUniqueException() {
    when(roleRepository.existsByNameIgnoreCase(ROLE_NAME)).thenReturn(true);
    assertThrows(
        NotUniqueException.class, () -> roleService.createNewRole(new RoleRequest(ROLE_NAME)));
  }

  @Test
  void getRoleById_whenRoleExists_thenReturnRole() {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(createRoleEntity(1L, ROLE_NAME)));
    when(roleMapper.fromEntity(any())).thenReturn(createRole(1L, ROLE_NAME));

    assertTrue(roleService.getRoleById(1L).isPresent());
  }

  @Test
  void getRoleById_whenRoleDoesNotExist_thenReturnEmpty() {
    when(roleRepository.findById(1L)).thenReturn(Optional.empty());

    assertTrue(roleService.getRoleById(1L).isEmpty());
  }

  @Test
  void getAllRoles_whenRolesExist_thenReturnPagedResponse() {
    Page<RoleEntity> page = new PageImpl<>(List.of(createRoleEntity(1L, ROLE_NAME)));

    when(roleRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(roleMapper.fromEntity(any())).thenReturn(createRole(1L, ROLE_NAME));

    assertEquals(1, roleService.getAllRoles(0, 10).getTotalPages());
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameIsUnique_thenReturnRoleId() {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(createRoleEntity(1L, ROLE_NAME)));
    when(roleMapper.fromEntity(any())).thenReturn(createRole(1L, ROLE_NAME));
    when(roleRepository.existsByNameIgnoreCase("USER")).thenReturn(false);
    when(roleRepository.save(any())).thenReturn(createRoleEntity(1L, "USER"));

    assertEquals(1L, roleService.updateRole(1L, new RoleRequest("USER")));
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameAlreadyExists_thenThrowNotUniqueException() {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(createRoleEntity(1L, ROLE_NAME)));
    when(roleMapper.fromEntity(any())).thenReturn(createRole(1L, ROLE_NAME));
    when(roleRepository.existsByNameIgnoreCase("USER")).thenReturn(true);

    assertThrows(
        NotUniqueException.class, () -> roleService.updateRole(1L, new RoleRequest("USER")));
  }

  @Test
  void updateRole_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(roleRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(
        NotFoundException.class, () -> roleService.updateRole(1L, new RoleRequest("USER")));
  }

  @Test
  public void updateRole_whenRoleExistsAndNewNameIsSame_thenDoNotThrowException() {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(createRoleEntity(1L, ROLE_NAME)));
    when(roleMapper.fromEntity(any())).thenReturn(createRole(1L, ROLE_NAME));
    when(roleRepository.existsByNameIgnoreCase(ROLE_NAME)).thenReturn(true);
    when(roleRepository.save(any())).thenReturn(createRoleEntity(1L, ROLE_NAME));

    assertDoesNotThrow(() -> roleService.updateRole(1L, new RoleRequest(ROLE_NAME)));
  }

  @Test
  void deleteRole_whenRoleExists_thenDeleteSuccessfully() {
    when(roleRepository.existsById(1L)).thenReturn(true);
    assertDoesNotThrow(() -> roleService.deleteRole(1L));
  }

  @Test
  void deleteRole_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(roleRepository.existsById(1L)).thenReturn(false);
    assertThrows(NotFoundException.class, () -> roleService.deleteRole(1L));
  }

  @Test
  void deleteRole_whenRoleHasDependencies_thenThrowDeletionNotAllowedException() {
    when(roleRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(roleRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> roleService.deleteRole(1L));
  }

  @Test
  void deleteRole_whenDataIntegrityViolationIsNotConstraintRelated_thenThrowException() {
    when(roleRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(roleRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> roleService.deleteRole(1L));
  }
}
