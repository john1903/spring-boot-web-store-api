package me.jangluzniewicz.webstore.roles.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
  @Mock private RoleRepository roleRepository;
  @Mock private RoleMapper roleMapper;
  @InjectMocks private RoleService roleService;

  @Test
  public void shouldCreateNewRoleAndReturnRoleId() {
    RoleRequest roleRequest = new RoleRequest("ADMIN");
    RoleEntity savedEntity = new RoleEntity(1L, "ADMIN");

    when(roleRepository.existsByNameIgnoreCase(roleRequest.getName())).thenReturn(false);
    when(roleMapper.toEntity(any())).thenReturn(new RoleEntity(null, "ADMIN"));
    when(roleRepository.save(any())).thenReturn(savedEntity);

    Long roleId = roleService.createNewRole(roleRequest);

    assertEquals(1L, roleId);
  }

  @Test
  public void shouldThrowNotUniqueExceptionWhenRoleAlreadyExists() {
    RoleRequest roleRequest = new RoleRequest("ADMIN");

    when(roleRepository.existsByNameIgnoreCase(roleRequest.getName())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> roleService.createNewRole(roleRequest));
  }

  @Test
  public void shouldReturnRoleWhenGettingRoleById() {
    RoleEntity roleEntity = new RoleEntity(1L, "ADMIN");

    when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
    when(roleMapper.fromEntity(roleEntity)).thenReturn(new Role(1L, "ADMIN"));

    Optional<Role> role = roleService.getRoleById(1L);

    assertTrue(role.isPresent());
    assertEquals(1L, role.get().getId());
    assertEquals("ADMIN", role.get().getName());
  }

  @Test
  public void shouldReturnEmptyWhenRoleNotFoundById() {
    when(roleRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<Role> role = roleService.getRoleById(1L);

    assertTrue(role.isEmpty());
  }

  @Test
  public void shouldReturnPagedResponseWhenGettingAllRoles() {
    RoleEntity roleEntity = new RoleEntity(1L, "ADMIN");
    Pageable pageable = PageRequest.of(0, 10);
    Page<RoleEntity> page = new PageImpl<>(List.of(roleEntity), pageable, 1);

    when(roleRepository.findAll(pageable)).thenReturn(page);
    when(roleMapper.fromEntity(roleEntity)).thenReturn(new Role(1L, "ADMIN"));

    PagedResponse<Role> roles = roleService.getAllRoles(0, 10);

    assertEquals(1, roles.getTotalPages());
    assertEquals(1, roles.getContent().size());
    assertEquals(1L, roles.getContent().getFirst().getId());
    assertEquals("ADMIN", roles.getContent().getFirst().getName());
  }

  @Test
  public void shouldUpdateRoleAndReturnRoleId() {
    RoleRequest roleRequest = new RoleRequest("USER");
    RoleEntity roleEntity = new RoleEntity(1L, "ADMIN");

    when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
    when(roleRepository.existsByNameIgnoreCase(roleRequest.getName())).thenReturn(false);

    Long roleId = roleService.updateRole(1L, roleRequest);

    assertEquals(1L, roleId);
  }

  @Test
  public void shouldThrowNotUniqueExceptionWhenRoleAlreadyExistsOnUpdate() {
    RoleRequest roleRequest = new RoleRequest("USER");
    RoleEntity roleEntity = new RoleEntity(1L, "ADMIN");

    when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
    when(roleRepository.existsByNameIgnoreCase(roleRequest.getName())).thenReturn(true);

    assertThrows(NotUniqueException.class, () -> roleService.updateRole(1L, roleRequest));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenRoleNotFoundOnUpdate() {
    RoleRequest roleRequest = new RoleRequest("USER");

    when(roleRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> roleService.updateRole(1L, roleRequest));
  }

  @Test
  public void shouldNotThrowExceptionWhenUpdatingRoleWithSameName() {
    RoleRequest roleRequest = new RoleRequest("ADMIN");
    RoleEntity roleEntity = new RoleEntity(1L, "ADMIN");

    when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
    when(roleRepository.existsByNameIgnoreCase(roleRequest.getName())).thenReturn(true);

    assertDoesNotThrow(() -> roleService.updateRole(1L, roleRequest));
  }

  @Test
  public void shouldDeleteRoleById() {
    when(roleRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> roleService.deleteRole(1L));
  }

  @Test
  public void shouldThrowNotFoundExceptionWhenRoleNotFoundOnDelete() {
    when(roleRepository.existsById(1L)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> roleService.deleteRole(1L));
  }

  @Test
  public void shouldThrowDeletionNotAllowedExceptionWhenDeletingRoleWithDependencies() {
    when(roleRepository.existsById(1L)).thenReturn(true);
    doThrow(
            new DataIntegrityViolationException(
                "", new ConstraintViolationException("", new SQLException(), "")))
        .when(roleRepository)
        .deleteById(1L);

    assertThrows(DeletionNotAllowedException.class, () -> roleService.deleteRole(1L));
  }

  @Test
  public void shouldThrowExceptionWhenWhenDataIntegrityViolationIsNotCausedByConstraintViolation() {
    when(roleRepository.existsById(1L)).thenReturn(true);
    doThrow(new DataIntegrityViolationException("", new SQLException()))
        .when(roleRepository)
        .deleteById(1L);

    assertThrows(DataIntegrityViolationException.class, () -> roleService.deleteRole(1L));
  }
}
