package me.jangluzniewicz.webstore.orderstatuses.e2e.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.e2e.config.E2ETest;
import me.jangluzniewicz.webstore.utils.e2e.security.WithCustomUser;
import me.jangluzniewicz.webstore.utils.testdata.order_statuses.OrderStatusRequestTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class OrderStatusControllerTest extends E2ETest {
  private static final String BASE_URL = "/order-statuses";
  private static final long VALID_ORDER_STATUS_ID = 2L;
  private static final long INVALID_ORDER_STATUS_ID = 999L;

  @ParameterizedTest
  @MethodSource("provideGetOrderStatusTestData")
  @DisplayName("GET /order-statuses")
  void getOrderStatusTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideGetOrderStatusTestData() {
    return Stream.of(
        Arguments.of(BASE_URL, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + VALID_ORDER_STATUS_ID, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + INVALID_ORDER_STATUS_ID, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideCreateOrderStatusTestData")
  @DisplayName("POST /order-statuses")
  @WithCustomUser(roles = {"ADMIN"})
  void createOrderStatusTests(String orderStatusRequest, HttpStatus expectedStatus)
      throws Exception {
    performPost(BASE_URL, orderStatusRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideCreateOrderStatusTestData() {
    String validOrderStatusRequest =
        OrderStatusRequestTestDataBuilder.builder().name("NEW_ORDER_STATUS").build().toJson();
    String duplicateOrderStatusRequest =
        OrderStatusRequestTestDataBuilder.builder().build().toJson();
    String invalidOrderStatusRequest = "{}";
    return Stream.of(
        Arguments.of(validOrderStatusRequest, HttpStatus.CREATED),
        Arguments.of(duplicateOrderStatusRequest, HttpStatus.CONFLICT),
        Arguments.of(invalidOrderStatusRequest, HttpStatus.BAD_REQUEST));
  }

  @ParameterizedTest
  @MethodSource("provideUpdateOrderStatusTestData")
  @DisplayName("PUT /order-statuses")
  @WithCustomUser(roles = {"ADMIN"})
  void updateOrderStatusTests(String url, String orderStatusRequest, HttpStatus expectedStatus)
      throws Exception {
    performPut(url, orderStatusRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUpdateOrderStatusTestData() {
    String validUpdateRequest =
        OrderStatusRequestTestDataBuilder.builder().name("UPDATED").build().toJson();
    String invalidUpdateRequest = "{}";
    String duplicateUpdateRequest = OrderStatusRequestTestDataBuilder.builder().build().toJson();

    return Stream.of(
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_STATUS_ID, validUpdateRequest, HttpStatus.NO_CONTENT),
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_STATUS_ID, invalidUpdateRequest, HttpStatus.BAD_REQUEST),
        Arguments.of(
            BASE_URL + "/" + INVALID_ORDER_STATUS_ID, validUpdateRequest, HttpStatus.NOT_FOUND),
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_STATUS_ID, duplicateUpdateRequest, HttpStatus.CONFLICT));
  }

  @ParameterizedTest
  @MethodSource("provideDeleteOrderStatusTestData")
  @DisplayName("DELETE /order-statuses")
  @WithCustomUser(roles = {"ADMIN"})
  void deleteOrderStatusTests(String url, HttpStatus expectedStatus) throws Exception {
    performDelete(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideDeleteOrderStatusTestData() {
    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_ORDER_STATUS_ID, HttpStatus.NO_CONTENT),
        Arguments.of(BASE_URL + "/" + INVALID_ORDER_STATUS_ID, HttpStatus.NOT_FOUND));
  }
}
