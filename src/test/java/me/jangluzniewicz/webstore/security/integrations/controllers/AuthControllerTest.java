package me.jangluzniewicz.webstore.security.integrations.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class AuthControllerTest extends IntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void login_whenUserExistsAndCredentialsAreValid_thenReturnOkAndToken() throws Exception {
    String loginRequest =
        """
        {
          "username": "admin@admin.com",
          "password": "admin"
        }
        """;

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.token", is(notNullValue())));
  }

  @Test
  void login_whenUserDoesNotExist_thenReturnUnauthorized() throws Exception {
    String loginRequest =
        """
        {
          "username": "new@new.com",
          "password": "new"
        }
        """;

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void login_whenRequestInvalid_thenReturnUnauthorized() throws Exception {
    String loginRequest = "{}";

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void createUser_whenRequestValid_thenReturnCreatedAndIdResponse() throws Exception {
    String userRequest =
        """
        {
          "roleId": 2,
          "email": "new@new.com",
          "password": "new",
          "phoneNumber": "+48123456789"
        }
        """;

    mockMvc
        .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(notNullValue())));
  }

  @Test
  void createUser_whenEmailExists_thenReturnConflict() throws Exception {
    String userRequest =
        """
        {
          "roleId": 2,
          "email": "client@client.com",
          "password": "client",
          "phoneNumber": "+48123456789"
          }
        """;

    mockMvc
        .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isConflict());
  }

  @Test
  void createUser_whenRequestInvalid_thenReturnBadRequest() throws Exception {
    String userRequest = "{}";

    mockMvc
        .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createUser_whenRoleIdAdminAndUserAdmin_thenReturnCreatedAndIdResponse() throws Exception {
    String userRequest =
        """
        {
          "roleId": 1,
          "email": "new_admin@admin.com",
          "password": "new_admin",
          "phoneNumber": "+48123456789"
          }
        """;

    mockMvc
        .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(notNullValue())));
  }

  @Test
  void createUser_whenRoleIdAdminAndUserNotAdmin_thenReturnForbidden() throws Exception {
    String userRequest =
        """
        {
          "roleId": 1,
          "email": "new_admin@admin.com",
          "password": "new_admin",
          "phoneNumber": "+48123456789"
        }
        """;

    mockMvc
        .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isForbidden());
  }
}
