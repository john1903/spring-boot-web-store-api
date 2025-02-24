package me.jangluzniewicz.webstore.roles.integrations.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class RoleControllerTest extends IntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void getRoles_whenRolesExist_thenReturnOkAndPagedResponse() throws Exception {
    mockMvc
        .perform(get("/roles"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", is(notNullValue())))
        .andExpect(jsonPath("$.totalPages", is(greaterThan(0))));
  }

  @Test
  void getRole_whenRoleExists_thenReturnOkAndRole() throws Exception {
    mockMvc
        .perform(get("/roles/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", not(emptyOrNullString())));
  }

  @Test
  void getRole_whenRoleDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(get("/roles/999")).andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createRole_whenRequestValid_thenReturnCreatedAndIdResponse() throws Exception {
    String roleRequest =
        """
        {
          "name": "TEST"
        }
        """;

    mockMvc
        .perform(post("/roles").contentType(MediaType.APPLICATION_JSON).content(roleRequest))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(notNullValue())));
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createRole_whenRoleNameExists_thenReturnConflict() throws Exception {
    String roleRequest =
        """
        {
          "name": "CUSTOMER"
        }
        """;

    mockMvc
        .perform(post("/roles").contentType(MediaType.APPLICATION_JSON).content(roleRequest))
        .andExpect(status().isConflict());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createRole_whenRequestInvalid_thenReturnBadRequest() throws Exception {
    String roleRequest = "{}";

    mockMvc
        .perform(post("/roles").contentType(MediaType.APPLICATION_JSON).content(roleRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateRole_whenRoleExistsAndRequestValid_thenReturnNoContent() throws Exception {
    String roleRequest =
        """
        {
          "name": "TEST"
        }
        """;

    mockMvc
        .perform(put("/roles/1").contentType(MediaType.APPLICATION_JSON).content(roleRequest))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateRole_whenRoleExistsAndRoleNameExists_thenReturnConflict() throws Exception {
    String roleRequest =
        """
        {
          "name": "CUSTOMER"
        }
        """;

    mockMvc
        .perform(put("/roles/1").contentType(MediaType.APPLICATION_JSON).content(roleRequest))
        .andExpect(status().isConflict());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateRole_whenRoleDoesNotExist_thenReturnNotFound() throws Exception {
    String roleRequest =
        """
        {
          "name": "TEST"
        }
        """;

    mockMvc
        .perform(put("/roles/999").contentType(MediaType.APPLICATION_JSON).content(roleRequest))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteRole_whenRoleExists_thenReturnNoContent() throws Exception {
    mockMvc.perform(delete("/roles/3")).andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteRole_whenRoleDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(delete("/roles/999")).andExpect(status().isNotFound());
  }
}
