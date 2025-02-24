package me.jangluzniewicz.webstore.categories.integrations.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class CategoryControllerTest extends IntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void getCategories_whenCategoriesExist_thenReturnOkAndPagedResponse() throws Exception {
    mockMvc
        .perform(get("/categories"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", is(notNullValue())))
        .andExpect(jsonPath("$.totalPages", is(greaterThanOrEqualTo(0))));
  }

  @Test
  void getCategory_whenCategoryExists_thenReturnOkAndCategory() throws Exception {
    mockMvc
        .perform(get("/categories/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", not(emptyOrNullString())));
  }

  @Test
  void getCategory_whenCategoryDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(get("/categories/999")).andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createCategory_whenRequestValid_thenReturnCreatedAndIdResponse() throws Exception {
    String categoryRequest =
        """
        {
          "name": "NEW_CATEGORY"
        }
        """;

    mockMvc
        .perform(
            post("/categories").contentType(MediaType.APPLICATION_JSON).content(categoryRequest))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(notNullValue())));
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createCategory_whenRequestInvalid_thenReturnBadRequest() throws Exception {
    String categoryRequest = "{}";

    mockMvc
        .perform(
            post("/categories").contentType(MediaType.APPLICATION_JSON).content(categoryRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createCategory_whenCategoryNameExists_thenReturnConflict() throws Exception {
    String categoryRequest =
        """
        {
          "name": "ELECTRONICS"
        }
        """;

    mockMvc
        .perform(
            post("/categories").contentType(MediaType.APPLICATION_JSON).content(categoryRequest))
        .andExpect(status().isConflict());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateCategory_whenCategoryExistsAndRequestValid_thenReturnNoContent() throws Exception {
    String categoryRequest =
        """
        {
          "name": "UPDATED"
        }
        """;

    mockMvc
        .perform(
            put("/categories/1").contentType(MediaType.APPLICATION_JSON).content(categoryRequest))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateCategory_whenCategoryExistsAndRequestInvalid_thenReturnBadRequest() throws Exception {
    String categoryRequest = "{}";

    mockMvc
        .perform(
            put("/categories/1").contentType(MediaType.APPLICATION_JSON).content(categoryRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateCategory_whenCategoryDoesNotExist_thenReturnNotFound() throws Exception {
    String categoryRequest =
        """
        {
          "name": "UPDATED"
        }
        """;

    mockMvc
        .perform(
            put("/categories/999").contentType(MediaType.APPLICATION_JSON).content(categoryRequest))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateCategory_whenCategoryExistsAndCategoryNameExists_thenReturnConflict()
      throws Exception {
    String categoryRequest =
        """
        {
          "name": "ELECTRONICS"
        }
        """;

    mockMvc
        .perform(
            put("/categories/2").contentType(MediaType.APPLICATION_JSON).content(categoryRequest))
        .andExpect(status().isConflict());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteCategory_whenCategoryExists_thenReturnNoContent() throws Exception {
    mockMvc.perform(delete("/categories/3")).andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteCategory_whenCategoryDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(delete("/categories/999")).andExpect(status().isNotFound());
  }
}
