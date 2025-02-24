package me.jangluzniewicz.webstore.users.integrations.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class UserControllerTest extends IntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void getUsers_whenUsersExist_thenReturnOkAndPagedResponse() throws Exception {
    mockMvc
        .perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", is(notNullValue())))
        .andExpect(jsonPath("$.totalPages", is(greaterThanOrEqualTo(0))));
  }

  @Test
  @WithCustomUser(id = 2)
  void getUser_whenUserExists_thenReturnOkAndUser() throws Exception {
    mockMvc
        .perform(get("/users/2"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.email", not(emptyOrNullString())));
  }

  @Test
  @WithCustomUser(id = 999)
  void getUser_whenUserExistsAndIdIsDifferent_thenReturnForbidden() throws Exception {
    mockMvc.perform(get("/users/2")).andExpect(status().isForbidden());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void getUser_whenUserDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(get("/users/999")).andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(id = 2)
  void updateUser_whenUserExistsAndRequestValid_thenReturnNoContent() throws Exception {
    String userRequest =
        """
        {
          "roleId": 2,
          "email": "client@client.com",
          "password": "client",
          "phoneNumber": "123456789"
        }
        """;

    mockMvc
        .perform(put("/users/2").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(id = 2)
  void updateUser_whenUserExistsAndUserTriesToUpdateRoleToAdmin_thenReturnForbidden()
      throws Exception {
    String userRequest =
        """
        {
          "roleId": 1,
          "email": "client@client.com",
          "password": "client",
          "phoneNumber": "222222222"
        }
        """;

    mockMvc
        .perform(put("/users/2").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithCustomUser(id = 2)
  void updateUser_whenUserExistsAndUserTriesToUpdateAnotherUser_thenReturnForbidden()
      throws Exception {
    String userRequest =
        """
        {
          "roleId": 2,
          "email": "admin@admin.com",
          "password": "admin",
          "phoneNumber": "111111111"
        }
        """;

    mockMvc
        .perform(put("/users/1").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateUser_whenUserExistsAndRequestInvalid_thenReturnBadRequest() throws Exception {
    String userRequest = "{}";

    mockMvc
        .perform(put("/users/2").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateUser_whenUserExistsAndAdminUpdatesRoleToAdmin_thenReturnNoContent() throws Exception {
    String userRequest =
        """
        {
          "roleId": 1,
          "email": "client@client.com",
          "password": "client",
          "phoneNumber": "222222222"
        }
        """;

    mockMvc
        .perform(put("/users/2").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateUser_whenUserDoesNotExist_thenReturnNotFound() throws Exception {
    String userRequest =
        """
        {
          "roleId": 2,
          "email": "client@client.com",
          "password": "client",
          "phoneNumber": "123456789"
        }
        """;

    mockMvc.perform(put("/users/999").contentType(MediaType.APPLICATION_JSON).content(userRequest));
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteUser_whenUserExists_thenReturnNoContent() throws Exception {
    mockMvc.perform(delete("/users/3")).andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteUser_whenUserDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(delete("/users/999")).andExpect(status().isNotFound());
  }
}
