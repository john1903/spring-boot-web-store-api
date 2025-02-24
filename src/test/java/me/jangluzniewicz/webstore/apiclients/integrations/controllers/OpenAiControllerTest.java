package me.jangluzniewicz.webstore.apiclients.integrations.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class OpenAiControllerTest extends IntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void getSeoDescription_whenProductDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(get("/products/999/seo-description")).andExpect(status().isNotFound());
  }

  @Test
  void getSeoDescription_whenUserIsNotAdmin_thenReturnForbidden() throws Exception {
    mockMvc.perform(get("/products/1/seo-description")).andExpect(status().isForbidden());
  }
}
