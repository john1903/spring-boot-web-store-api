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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
  @Mock private RoleRepository roleRepository;
  @Mock private RoleMapper roleMapper;
  @InjectMocks private RoleService roleService;

  @Test
  void createNewRole_whenRoleDoesNotExist_thenReturnRoleId() {
    when(roleRepository.existsByNameIgnoreCase("ADMIN")).thenReturn(false);
    when(roleRepository.save(any())).thenReturn(RoleEntity.builder().id(1L).name("ADMIN").build());

    assertEquals(1L, roleService.createNewRole(new RoleRequest("ADMIN")));
  }

  @Test
  void createNewRole_whenRoleAlreadyExists_thenThrowNotUniqueException() {
    when(roleRepository.existsByNameIgnoreCase("ADMIN")).thenReturn(true);

    assertThrows(
        NotUniqueException.class, () -> roleService.createNewRole(new RoleRequest("ADMIN")));
  }

  @Test
  void getRoleById_whenRoleExists_thenReturnRole() {
    when(roleRepository.findById(1L))
        .thenReturn(Optional.of(RoleEntity.builder().id(1L).name("ADMIN").build()));
    when(roleMapper.fromEntity(any())).thenReturn(Role.builder().id(1L).name("ADMIN").build());

    assertTrue(roleService.getRoleById(1L).isPresent());
  }

  @Test
  void getRoleById_whenRoleDoesNotExist_thenReturnEmpty() {
    when(roleRepository.findById(1L)).thenReturn(Optional.empty());

    assertTrue(roleService.getRoleById(1L).isEmpty());
  }

  @Test
  void getAllRoles_whenRolesExist_thenReturnPagedResponse() {
    when(roleRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(RoleEntity.builder().id(1L).name("ADMIN").build())));
    when(roleMapper.fromEntity(any())).thenReturn(Role.builder().id(1L).name("ADMIN").build());

    assertEquals(1, roleService.getAllRoles(0, 10).getTotalPages());
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameIsUnique_thenReturnRoleId() {
    when(roleRepository.findById(1L))
        .thenReturn(Optional.of(RoleEntity.builder().id(1L).name("ADMIN").build()));
    when(roleMapper.fromEntity(any())).thenReturn(Role.builder().id(1L).name("ADMIN").build());
    when(roleRepository.existsByNameIgnoreCase("USER")).thenReturn(false);
    when(roleRepository.save(any())).thenReturn(RoleEntity.builder().id(1L).name("USER").build());

    assertEquals(1L, roleService.updateRole(1L, new RoleRequest("USER")));
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameAlreadyExists_thenThrowNotUniqueException() {
    when(roleRepository.findById(1L))
        .thenReturn(Optional.of(RoleEntity.builder().id(1L).name("ADMIN").build()));
    when(roleMapper.fromEntity(any())).thenReturn(Role.builder().id(1L).name("ADMIN").build());
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
    when(roleRepository.findById(1L))
        .thenReturn(Optional.of(RoleEntity.builder().id(1L).name("ADMIN").build()));
    when(roleMapper.fromEntity(any())).thenReturn(Role.builder().id(1L).name("ADMIN").build());
    when(roleRepository.existsByNameIgnoreCase("ADMIN")).thenReturn(true);
    when(roleRepository.save(any())).thenReturn(RoleEntity.builder().id(1L).name("ADMIN").build());

    assertDoesNotThrow(() -> roleService.updateRole(1L, new RoleRequest("ADMIN")));
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
