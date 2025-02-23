package me.jangluzniewicz.webstore.orderstatuses.integartions.controllers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class OrderStatusControllerTest extends IntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void getOrderStatuses_whenOrderStatusesExists_shouldReturnOkAndPagedResponse() throws Exception {
    mockMvc
        .perform(get("/order-statuses"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", is(notNullValue())))
        .andExpect(jsonPath("$.totalPages", is(greaterThanOrEqualTo(0))));
  }

  @Test
  void getOrderStatus_whenOrderStatusExists_shouldReturnOkAndOrderStatus() throws Exception {
    mockMvc
        .perform(get("/order-statuses/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", not(emptyOrNullString())));
  }

  @Test
  void getOrderStatus_whenOrderStatusDoesNotExist_shouldReturnNotFound() throws Exception {
    mockMvc.perform(get("/order-statuses/999")).andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createOrderStatus_whenRequestValid_shouldReturnCreatedAndIdResponse() throws Exception {
    String orderStatusRequest = "{\"name\": \"NEW_ORDER_STATUS\"}";

    mockMvc
        .perform(
            post("/order-statuses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(orderStatusRequest))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(notNullValue())));
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void createOrderStatus_whenOrderStatusNameExists_shouldReturnConflict() throws Exception {
    String orderStatusRequest = "{\"name\": \"APPROVED\"}";

    mockMvc
        .perform(
            post("/order-statuses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(orderStatusRequest))
        .andExpect(status().isConflict());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateOrderStatus_whenOrderStatusExistsAndRequestValid_shouldReturnNoContent()
      throws Exception {
    String orderStatusRequest = "{\"name\": \"UPDATED_ORDER_STATUS\"}";

    mockMvc
        .perform(
            put("/order-statuses/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(orderStatusRequest))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateOrderStatus_whenOrderStatusExistsAndRequestInvalid_shouldReturnBadRequest()
      throws Exception {
    String orderStatusRequest = "{}";

    mockMvc
        .perform(
            put("/order-statuses/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(orderStatusRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateOrderStatus_whenOrderStatusDoesNotExist_shouldReturnNotFound() throws Exception {
    String orderStatusRequest = "{\"name\": \"UPDATED_ORDER_STATUS\"}";

    mockMvc
        .perform(
            put("/order-statuses/999")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(orderStatusRequest))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void updateOrderStatus_whenOrderStatusExistsAndNameExists_shouldReturnConflict()
      throws Exception {
    String orderStatusRequest = "{\"name\": \"APPROVED\"}";

    mockMvc
        .perform(
            put("/order-statuses/3")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(orderStatusRequest))
        .andExpect(status().isConflict());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteOrderStatus_whenOrderStatusExists_shouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/order-statuses/2")).andExpect(status().isNoContent());
  }

  @Test
  @WithCustomUser(roles = {"ADMIN"})
  void deleteOrderStatus_whenOrderStatusDoesNotExist_shouldReturnNotFound() throws Exception {
    mockMvc.perform(delete("/order-statuses/999")).andExpect(status().isNotFound());
  }
}
