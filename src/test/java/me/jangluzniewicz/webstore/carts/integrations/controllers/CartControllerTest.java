package me.jangluzniewicz.webstore.carts.integrations.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class CartControllerTest extends IntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  @WithCustomUser(id = 2)
  void getCart_whenCartExists_thenReturnCart() throws Exception {
    mockMvc
        .perform(get("/carts/current"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.customerId").value(2))
        .andExpect(jsonPath("$.items", is(notNullValue())));
  }

  @Test
  @WithCustomUser(id = 3)
  void getCart_whenCartDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(get("/carts/current")).andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(id = 2)
  void addItemToCart_whenProductExists_thenReturnNoContent() throws Exception {
    String cartItemRequest =
        """
        {
          "productId": 1,
          "quantity": 1
        }
        """;

    mockMvc
        .perform(
            post("/carts/current/items")
                .content(cartItemRequest)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(id = 2)
  void addItemToCart_whenProductDoesNotExist_thenReturnNotFound() throws Exception {
    String cartItemRequest =
        """
        {
          "productId": 999,
          "quantity": 1
        }
        """;

    mockMvc
        .perform(
            post("/carts/current/items")
                .content(cartItemRequest)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(id = 2)
  void emptyCart_whenCartExists_thenReturnNoContent() throws Exception {
    mockMvc.perform(delete("/carts/current/items")).andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(id = 3)
  void emptyCart_whenCartDoesNotExist_thenReturnNotFound() throws Exception {
    mockMvc.perform(delete("/carts/current/items")).andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(id = 2)
  void updateCart_whenCartExistsAndProductExists_thenReturnNoContent() throws Exception {
    String cartRequest =
        """
        {
          "items": [
            {
              "productId": 1,
              "quantity": 2
            }
          ]
        }
        """;

    mockMvc
        .perform(put("/carts/current").content(cartRequest).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(id = 2)
  void updateCart_whenCartExistsAndProductDoesNotExist_thenReturnNotFound() throws Exception {
    String cartRequest =
        """
        {
          "items": [
            {
              "productId": 999,
              "quantity": 2
            }
          ]
        }
        """;

    mockMvc
        .perform(put("/carts/current").content(cartRequest).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(id = 3)
  void updateCart_whenCartDoesNotExist_thenReturnNotFound() throws Exception {
    String cartRequest =
        """
        {
          "items": [
            {
              "productId": 1,
              "quantity": 2
            }
          ]
        }
        """;

    mockMvc
        .perform(put("/carts/current").content(cartRequest).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
