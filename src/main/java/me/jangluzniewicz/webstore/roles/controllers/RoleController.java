package me.jangluzniewicz.webstore.roles.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import me.jangluzniewicz.webstore.commons.models.IdResponse;
import me.jangluzniewicz.webstore.commons.models.PagedResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.roles.interfaces.IRole;
import me.jangluzniewicz.webstore.roles.models.Role;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Roles", description = "Operations related to roles")
@RestController
@RequestMapping("/roles")
public class RoleController {
  private final IRole roleService;

  public RoleController(IRole roleService) {
    this.roleService = roleService;
  }

  @Operation(summary = "Get roles", description = "Returns a paginated list of roles")
  @ApiResponse(
      responseCode = "200",
      description = "List of roles",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = PagedResponse.class)))
  @GetMapping
  public ResponseEntity<PagedResponse<Role>> getRoles(
      @Parameter(in = ParameterIn.QUERY, description = "Page number", example = "0")
          @RequestParam(defaultValue = "0")
          Integer page,
      @Parameter(in = ParameterIn.QUERY, description = "Page size", example = "20")
          @RequestParam(defaultValue = "20")
          Integer size) {
    return ResponseEntity.ok(roleService.getAllRoles(page, size));
  }

  @Operation(summary = "Get role by ID", description = "Returns a role based on the provided ID")
  @ApiResponse(
      responseCode = "200",
      description = "Role found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Role.class)))
  @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
  @GetMapping("/{id}")
  public ResponseEntity<Role> getRole(
      @Parameter(in = ParameterIn.PATH, description = "Role ID", required = true, example = "1")
          @PathVariable
          Long id) {
    return roleService
        .getRoleById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new NotFoundException("Role not found"));
  }

  @Operation(
      summary = "Create role",
      description = "Creates a new role (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(
      responseCode = "201",
      description = "Role created",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = IdResponse.class)))
  @ApiResponse(responseCode = "409", description = "Role name already exists", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<IdResponse> createRole(
      @RequestBody(
              description = "Role creation payload",
              required = true,
              content = @Content(schema = @Schema(implementation = RoleRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          RoleRequest roleRequest) {
    IdResponse response = roleService.createNewRole(roleRequest);
    return ResponseEntity.created(URI.create("/roles/" + response.getId())).body(response);
  }

  @Operation(
      summary = "Update role",
      description = "Updates an existing role (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Role updated", content = @Content)
  @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
  @ApiResponse(responseCode = "409", description = "Role name already exists", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<Void> updateRole(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the role to update",
              required = true,
              example = "1")
          @PathVariable
          Long id,
      @RequestBody(
              description = "Role update payload",
              required = true,
              content = @Content(schema = @Schema(implementation = RoleRequest.class)))
          @Valid
          @org.springframework.web.bind.annotation.RequestBody
          RoleRequest roleRequest) {
    roleService.updateRole(id, roleRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Delete role",
      description = "Deletes a role by its ID (requires ADMIN role)",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponse(responseCode = "204", description = "Role deleted", content = @Content)
  @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
  @ApiResponse(responseCode = "409", description = "Role has associated users", content = @Content)
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(
      @Parameter(
              in = ParameterIn.PATH,
              description = "ID of the role to delete",
              required = true,
              example = "1")
          @PathVariable
          Long id) {
    roleService.deleteRole(id);
    return ResponseEntity.noContent().build();
  }
}
