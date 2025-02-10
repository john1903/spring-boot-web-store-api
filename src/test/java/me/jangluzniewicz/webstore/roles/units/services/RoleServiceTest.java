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
import me.jangluzniewicz.webstore.common.units.BaseServiceUnitTest;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.mappers.RoleMapper;
import me.jangluzniewicz.webstore.roles.repositories.RoleRepository;
import me.jangluzniewicz.webstore.roles.services.RoleService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest extends BaseServiceUnitTest {
  @Mock private RoleRepository roleRepository;
  @Mock private RoleMapper roleMapper;
  @InjectMocks private RoleService roleService;

  private RoleRequest roleRequest1;
  private RoleRequest roleRequest2;

  @BeforeEach
  void setUp() {
    roleRequest1 = new RoleRequest("ADMIN");
    roleRequest2 = new RoleRequest("USER");
  }

  @Test
  void createNewRole_whenRoleDoesNotExist_thenReturnRoleId() {
    when(roleRepository.existsByNameIgnoreCase(roleRequest1.getName())).thenReturn(false);
    when(roleRepository.save(any())).thenReturn(roleEntity);

    assertEquals(1L, roleService.createNewRole(roleRequest1));
  }

  @Test
  void createNewRole_whenRoleAlreadyExists_thenThrowNotUniqueException() {
    when(roleRepository.existsByNameIgnoreCase(roleRequest1.getName())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> roleService.createNewRole(roleRequest1));
  }

  @Test
  void getRoleById_whenRoleExists_thenReturnRole() {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
    when(roleMapper.fromEntity(any())).thenReturn(role);

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
        .thenReturn(new PageImpl<>(List.of(roleEntity)));
    when(roleMapper.fromEntity(any())).thenReturn(role);

    assertEquals(1, roleService.getAllRoles(0, 10).getTotalPages());
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameIsUnique_thenReturnRoleId() {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
    when(roleMapper.fromEntity(any())).thenReturn(role);
    when(roleRepository.existsByNameIgnoreCase(roleRequest2.getName())).thenReturn(false);
    RoleEntity updatedEntity =
        RoleEntity.builder().id(roleEntity.getId()).name(roleRequest2.getName()).build();
    when(roleRepository.save(any())).thenReturn(updatedEntity);

    assertEquals(1L, roleService.updateRole(1L, roleRequest2));
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameAlreadyExists_thenThrowNotUniqueException() {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
    when(roleMapper.fromEntity(any())).thenReturn(role);
    when(roleRepository.existsByNameIgnoreCase(roleRequest2.getName())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> roleService.updateRole(1L, roleRequest2));
  }

  @Test
  void updateRole_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(roleRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> roleService.updateRole(1L, roleRequest1));
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameIsSame_thenDoNotThrowException() {
    when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
    when(roleMapper.fromEntity(any())).thenReturn(role);
    when(roleRepository.existsByNameIgnoreCase(roleRequest1.getName())).thenReturn(true);
    when(roleRepository.save(any())).thenReturn(roleEntity);

    assertDoesNotThrow(() -> roleService.updateRole(1L, roleRequest1));
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
