package me.jangluzniewicz.webstore.roles.controllers;

import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.common.models.PagedResponse;
import me.jangluzniewicz.webstore.roles.interfaces.IRole;
import me.jangluzniewicz.webstore.roles.models.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/roles")
public class RoleController {
  private final IRole roleService;

  public RoleController(IRole roleService) {
    this.roleService = roleService;
  }

  @GetMapping
  public ResponseEntity<PagedResponse<Role>> getRoles(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    return ResponseEntity.ok(roleService.getAllRoles(page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Role> getRole(@PathVariable Long id) {
    return roleService
        .getRoleById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<Void> createRole(@Valid @RequestBody RoleRequest roleRequest) {
    Long roleId = roleService.createNewRole(roleRequest);
    return ResponseEntity.created(URI.create("/roles/" + roleId)).build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateRole(
      @PathVariable Long id, @Valid @RequestBody RoleRequest roleRequest) {
    roleService.updateRole(id, roleRequest);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
    return ResponseEntity.noContent().build();
  }
}
