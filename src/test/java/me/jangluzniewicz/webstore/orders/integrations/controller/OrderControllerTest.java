package me.jangluzniewicz.webstore.orders.integrations.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class OrderControllerTest extends IntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void getOrders_whenOrdersExist_thenReturnOkAndPagedResponse() throws Exception {
    mockMvc
        .perform(get("/orders"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", is(notNullValue())))
        .andExpect(jsonPath("$.totalPages", is(greaterThan(0))));
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void getOrders_whenOrdersExist_thenReturnOkAndFilteredPagedResponse() throws Exception {
    mockMvc
        .perform(
            get(
                "/orders?statusId=4&orderDateAfter="
                    + LocalDateTime.now().minusHours(1)
                    + "&orderDateBefore="
                    + LocalDateTime.now().plusHours(1)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", is(notNullValue())))
        .andExpect(jsonPath("$.totalPages", is(greaterThan(0))));
  }

  @Test
  void getOrders_whenUserIsNotAdmin_thenReturnForbidden() throws Exception {
    mockMvc.perform(get("/orders")).andExpect(status().isForbidden());
  }
}
