package me.jangluzniewicz.webstore.products.integrations.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

public class ProductControllerTest extends IntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void getProducts_whenProductsExist_thenReturnOkAndPagedResponse() throws Exception {
    mockMvc
        .perform(get("/products"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", is(notNullValue())))
        .andExpect(jsonPath("$.totalPages", is(greaterThanOrEqualTo(0))));
  }

  @Test
  void getProducts_whenProductsExist_thenReturnOkAndFilteredPagedResponse() throws Exception {
    mockMvc
        .perform(get("/products?categoryId=1&name=phone&priceFrom=90&priceTo=100"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", is(notNullValue())))
        .andExpect(jsonPath("$.totalPages", is(greaterThanOrEqualTo(0))));
  }

  @Test
  void getProduct_whenProductExists_thenReturnOkAndProduct() throws Exception {
    mockMvc
        .perform(get("/products/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", not(emptyOrNullString())));
  }

  @Test
  void getProduct_whenProductDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(get("/products/999")).andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createProduct_whenRequestValid_thenReturnCreatedAndIdResponse() throws Exception {
    String productRequest =
        """
        {
          "name": "NEW_PRODUCT",
          "description": "NEW_DESCRIPTION",
          "price": 150.25,
          "weight": 1.5,
          "categoryId": 1
        }
        """;

    mockMvc
        .perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(productRequest))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(notNullValue())));
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createProduct_whenRequestInvalid_thenReturnBadRequest() throws Exception {
    String productRequest = "{}";

    mockMvc
        .perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(productRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createProduct_whenUserIsNotAdmin_thenReturnForbidden() throws Exception {
    String productRequest =
        """
        {
          "name": "NEW_PRODUCT",
          "description": "NEW_DESCRIPTION",
          "price": 150.25,
          "weight": 1.5,
          "categoryId": 1
        }
        """;

    mockMvc
        .perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(productRequest))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createProductFromCsv_whenFileExists_thenReturnNoContent() throws Exception {
    String csv =
        """
        Headphones,Best sound quality,1200.0,0.2,1
        Keyboard,RGB backlight,200.0,0.5,1
        """;
    MockMultipartFile file =
        new MockMultipartFile("file", "products.csv", "text/csv", csv.getBytes());
    mockMvc.perform(multipart("/products/import/csv").file(file)).andExpect(status().isNoContent());
  }

  @Test
  void createProductFromCsv_whenUserIsNotAdmin_thenReturnForbidden() throws Exception {
    String csv =
        """
        Headphones,Best sound quality,1200.0,0.2,1
        Keyboard,RGB backlight,200.0,0.5,1
        """;
    MockMultipartFile file =
        new MockMultipartFile("file", "products.csv", "text/csv", csv.getBytes());
    mockMvc.perform(multipart("/products/import/csv").file(file)).andExpect(status().isForbidden());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateProduct_whenProductExistsAndRequestValid_thenReturnNoContent() throws Exception {
    String productRequest =
        """
        {
          "name": "UPDATED_PRODUCT",
          "description": "UPDATED_DESCRIPTION",
          "price": 150.25,
          "weight": 1.5,
          "categoryId": 1
        }
        """;

    mockMvc
        .perform(put("/products/1").contentType(MediaType.APPLICATION_JSON).content(productRequest))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateProduct_whenProductExistsAndRequestInvalid_thenReturnBadRequest() throws Exception {
    String productRequest = "{}";

    mockMvc
        .perform(put("/products/1").contentType(MediaType.APPLICATION_JSON).content(productRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateProduct_whenProductDoesNotExist_thenReturnNotFound() throws Exception {
    String productRequest =
        """
        {
          "name": "UPDATED_PRODUCT",
          "description": "UPDATED_DESCRIPTION",
          "price": 150.25,
          "weight": 1.5,
          "categoryId": 1
        }
        """;

    mockMvc
        .perform(
            put("/products/999").contentType(MediaType.APPLICATION_JSON).content(productRequest))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateProduct_whenUserIsNotAdmin_thenReturnForbidden() throws Exception {
    String productRequest =
        """
        {
          "name": "UPDATED_PRODUCT",
          "description": "UPDATED_DESCRIPTION",
          "price": 150.25,
          "weight": 1.5,
          "categoryId": 1
        }
        """;

    mockMvc
        .perform(put("/products/1").contentType(MediaType.APPLICATION_JSON).content(productRequest))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteProduct_whenProductExists_thenReturnNoContent() throws Exception {
    mockMvc.perform(delete("/products/1")).andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteProduct_whenProductDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(delete("/products/999")).andExpect(status().isNotFound());
  }

  @Test
  void deleteProduct_whenUserIsNotAdmin_thenReturnForbidden() throws Exception {
    mockMvc.perform(delete("/products/1")).andExpect(status().isForbidden());
  }
}
