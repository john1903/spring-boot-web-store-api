package me.jangluzniewicz.webstore.roles.controllers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoleRequest {
    @NotNull(message = "name is required")
    @Size(min = 1, max = 255, message = "name must be between 1 and 255 characters")
    private String name;
}
