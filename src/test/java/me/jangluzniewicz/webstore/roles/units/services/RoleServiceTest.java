package me.jangluzniewicz.webstore.roles.units.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import me.jangluzniewicz.webstore.roles.controllers.RoleRequest;
import me.jangluzniewicz.webstore.roles.entities.RoleEntity;
import me.jangluzniewicz.webstore.roles.mappers.RoleMapper;
import me.jangluzniewicz.webstore.roles.models.Role;
import me.jangluzniewicz.webstore.roles.repositories.RoleRepository;
import me.jangluzniewicz.webstore.roles.services.RoleService;
import me.jangluzniewicz.webstore.utils.testdata.roles.RoleEntityTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.roles.RoleRequestTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.roles.RoleTestDataBuilder;
import me.jangluzniewicz.webstore.utils.units.config.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class RoleServiceTest extends UnitTest {
  @Mock private RoleRepository roleRepository;
  @Mock private RoleMapper roleMapper;
  @InjectMocks private RoleService roleService;

  private RoleEntity roleEntity;
  private Role role;
  private RoleRequest roleRequest1;
  private RoleRequest roleRequest2;

  @BeforeEach
  void setUp() {
    roleEntity = RoleEntityTestDataBuilder.builder().build().buildRoleEntity();
    role = RoleTestDataBuilder.builder().build().buildRole();
    roleRequest1 = RoleRequestTestDataBuilder.builder().build().buildRoleRequest();
    roleRequest2 = RoleRequestTestDataBuilder.builder().name("ADMIN").build().buildRoleRequest();
  }

  @Test
  void createNewRole_whenRoleDoesNotExist_thenReturnIdResponse() {
    when(roleRepository.existsByNameIgnoreCase(roleRequest1.getName())).thenReturn(false);
    when(roleRepository.save(any())).thenReturn(roleEntity);

    assertEquals(roleEntity.getId(), roleService.createNewRole(roleRequest1).getId());
  }

  @Test
  void createNewRole_whenRoleAlreadyExists_thenThrowNotUniqueException() {
    when(roleRepository.existsByNameIgnoreCase(roleRequest1.getName())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> roleService.createNewRole(roleRequest1));
  }

  @Test
  void getRoleById_whenRoleExists_thenReturnRole() {
    when(roleRepository.findById(roleEntity.getId())).thenReturn(Optional.of(roleEntity));
    when(roleMapper.fromEntity(any())).thenReturn(role);

    assertTrue(roleService.getRoleById(roleEntity.getId()).isPresent());
  }

  @Test
  void getRoleById_whenRoleDoesNotExist_thenReturnEmpty() {
    when(roleRepository.findById(roleEntity.getId())).thenReturn(Optional.empty());

    assertTrue(roleService.getRoleById(roleEntity.getId()).isEmpty());
  }

  @Test
  void getAllRoles_whenRolesExist_thenReturnPagedResponse() {
    when(roleRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(roleEntity)));
    when(roleMapper.fromEntity(roleEntity)).thenReturn(role);

    assertEquals(1, roleService.getAllRoles(0, 10).getTotalPages());
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameIsUnique_thenUpdateRole() {
    when(roleRepository.findById(roleEntity.getId())).thenReturn(Optional.of(roleEntity));
    when(roleMapper.fromEntity(roleEntity)).thenReturn(role);
    when(roleRepository.existsByNameIgnoreCase(roleRequest2.getName())).thenReturn(false);
    RoleEntity updatedEntity =
        RoleEntityTestDataBuilder.builder().name(roleRequest2.getName()).build().buildRoleEntity();
    when(roleRepository.save(any())).thenReturn(updatedEntity);

    assertDoesNotThrow(() -> roleService.updateRole(roleEntity.getId(), roleRequest2));
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameAlreadyExists_thenThrowNotUniqueException() {
    when(roleRepository.findById(roleEntity.getId())).thenReturn(Optional.of(roleEntity));
    when(roleMapper.fromEntity(roleEntity)).thenReturn(role);
    when(roleRepository.existsByNameIgnoreCase(roleRequest2.getName())).thenReturn(true);

    assertThrows(
        NotUniqueException.class, () -> roleService.updateRole(roleEntity.getId(), roleRequest2));
  }

  @Test
  void updateRole_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(roleRepository.findById(roleEntity.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> roleService.updateRole(roleEntity.getId(), roleRequest1));
  }

  @Test
  void updateRole_whenRoleExistsAndNewNameIsSame_thenDoNotThrowException() {
    when(roleRepository.findById(roleEntity.getId())).thenReturn(Optional.of(roleEntity));
    when(roleMapper.fromEntity(roleEntity)).thenReturn(role);
    when(roleRepository.existsByNameIgnoreCase(roleRequest1.getName())).thenReturn(true);
    when(roleRepository.save(any())).thenReturn(roleEntity);

    assertDoesNotThrow(() -> roleService.updateRole(roleEntity.getId(), roleRequest1));
  }

  @Test
  void deleteRole_whenRoleExists_thenDeleteSuccessfully() {
    when(roleRepository.existsById(roleEntity.getId())).thenReturn(true);

    assertDoesNotThrow(() -> roleService.deleteRole(roleEntity.getId()));
  }

  @Test
  void deleteRole_whenRoleDoesNotExist_thenThrowNotFoundException() {
    when(roleRepository.existsById(roleEntity.getId())).thenReturn(false);

    assertThrows(NotFoundException.class, () -> roleService.deleteRole(roleEntity.getId()));
  }
}
