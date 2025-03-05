package me.jangluzniewicz.webstore.orders.e2e.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.e2e.config.E2ETest;
import me.jangluzniewicz.webstore.utils.e2e.security.WithCustomUser;
import me.jangluzniewicz.webstore.utils.testdata.orders.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class OrderControllerTest extends E2ETest {
  private static final String BASE_URL = "/orders";
  private static final long VALID_USER_ID = 2L;
  private static final long VALID_ORDER_WITH_NOT_APPROVED_STATUS_ID = 1L;
  private static final long VALID_ORDER_WITH_COMPLETED_STATUS_ID = 2L;
  private static final long VALID_ORDER_WITH_RATING_ID = 4L;
  private static final long VALID_ORDER_WITH_DIFFERENT_OWNER_ID = 5L;
  private static final long INVALID_ORDER_ID = 999L;
  private static final long ORDER_STATUS_ACCEPTED_ID = 2L;

  @ParameterizedTest
  @MethodSource("provideAdminGetOrderTestData")
  @DisplayName("GET /orders (ADMIN)")
  @WithCustomUser(roles = {"ADMIN"})
  void adminGetOrderTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideAdminGetOrderTestData() {
    String filterParams = OrderFilterRequestTestDataBuilder.builder().build().toRequestParams();

    return Stream.of(
        Arguments.of(BASE_URL, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + VALID_ORDER_WITH_NOT_APPROVED_STATUS_ID, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + INVALID_ORDER_ID, HttpStatus.NOT_FOUND),
        Arguments.of(BASE_URL + "?" + filterParams, HttpStatus.OK));
  }

  @ParameterizedTest
  @MethodSource("provideUserGetOrderTestData")
  @DisplayName("GET /orders (USER)")
  @WithCustomUser(id = VALID_USER_ID)
  void userGetOrderTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUserGetOrderTestData() {
    return Stream.of(
        Arguments.of(BASE_URL, HttpStatus.FORBIDDEN),
        Arguments.of(BASE_URL + "/" + VALID_ORDER_WITH_NOT_APPROVED_STATUS_ID, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + INVALID_ORDER_ID, HttpStatus.NOT_FOUND),
        Arguments.of(BASE_URL + "/" + VALID_ORDER_WITH_DIFFERENT_OWNER_ID, HttpStatus.FORBIDDEN));
  }

  @ParameterizedTest
  @MethodSource("provideCurrentUserGetOrderTestData")
  @DisplayName("GET /current")
  @WithCustomUser(id = VALID_USER_ID)
  void currentUserGetOrderTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideCurrentUserGetOrderTestData() {
    return Stream.of(Arguments.of(BASE_URL + "/current", HttpStatus.OK));
  }

  @ParameterizedTest
  @MethodSource("provideAdminCreateOrderTestData")
  @DisplayName("POST /orders (ADMIN)")
  @WithCustomUser(roles = {"ADMIN"})
  void adminCreateOrderTests(String orderRequest, HttpStatus expectedStatus) throws Exception {
    performPost(BASE_URL, orderRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideAdminCreateOrderTestData() {
    String validOrderRequest =
        OrderRequestTestDataBuilder.builder().customerId(VALID_USER_ID).build().toJson();
    String invalidOrderRequest = "{}";
    return Stream.of(
        Arguments.of(validOrderRequest, HttpStatus.CREATED),
        Arguments.of(invalidOrderRequest, HttpStatus.BAD_REQUEST));
  }

  @ParameterizedTest
  @MethodSource("provideUserCreateOrderTestData")
  @DisplayName("POST /orders (USER)")
  @WithCustomUser(id = VALID_USER_ID)
  void userCreateOrderTests(String orderRequest, HttpStatus expectedStatus) throws Exception {
    performPost(BASE_URL, orderRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUserCreateOrderTestData() {
    String validOrderRequest =
        OrderRequestTestDataBuilder.builder().customerId(VALID_USER_ID).build().toJson();
    String invalidOrderRequest = "{}";
    String orderForAnotherUser =
        OrderRequestTestDataBuilder.builder().customerId(VALID_USER_ID + 1).build().toJson();
    return Stream.of(
        Arguments.of(validOrderRequest, HttpStatus.CREATED),
        Arguments.of(invalidOrderRequest, HttpStatus.BAD_REQUEST),
        Arguments.of(orderForAnotherUser, HttpStatus.FORBIDDEN));
  }

  @ParameterizedTest
  @MethodSource("provideUpdateOrderTestData")
  @DisplayName("PUT /orders")
  @WithCustomUser(roles = {"ADMIN"})
  void adminUpdateOrderTests(String url, String orderRequest, HttpStatus expectedStatus)
      throws Exception {
    performPut(url, orderRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUpdateOrderTestData() {
    String validOrderRequest =
        OrderRequestTestDataBuilder.builder()
            .items(List.of(OrderItemRequestTestDataBuilder.builder().quantity(2).build()))
            .build()
            .toJson();
    String invalidOrderRequest = "{}";
    return Stream.of(
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_NOT_APPROVED_STATUS_ID,
            validOrderRequest,
            HttpStatus.NO_CONTENT),
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_NOT_APPROVED_STATUS_ID,
            invalidOrderRequest,
            HttpStatus.BAD_REQUEST),
        Arguments.of(BASE_URL + "/" + INVALID_ORDER_ID, validOrderRequest, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideUpdateOrderStatus")
  @DisplayName("PUT /orders/{orderId}/status")
  @WithCustomUser(roles = {"ADMIN"})
  void updateOrderStatusTests(String url, String orderStatusRequest, HttpStatus expectedStatus)
      throws Exception {
    performPut(url, orderStatusRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUpdateOrderStatus() {
    String validOrderStatusRequest =
        ChangeOrderStatusRequestTestDataBuilder.builder()
            .orderStatusId(ORDER_STATUS_ACCEPTED_ID)
            .build()
            .toJson();
    String invalidOrderStatusRequest = "{}";
    return Stream.of(
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_NOT_APPROVED_STATUS_ID + "/status",
            validOrderStatusRequest,
            HttpStatus.NO_CONTENT),
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_NOT_APPROVED_STATUS_ID + "/status",
            invalidOrderStatusRequest,
            HttpStatus.BAD_REQUEST),
        Arguments.of(
            BASE_URL + "/" + INVALID_ORDER_ID + "/status",
            validOrderStatusRequest,
            HttpStatus.NOT_FOUND),
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_COMPLETED_STATUS_ID + "/status",
            validOrderStatusRequest,
            HttpStatus.CONFLICT));
  }

  @ParameterizedTest
  @MethodSource("provideAdminRateOrderTestData")
  @DisplayName("POST /orders/{orderId}/rating (ADMIN)")
  @WithCustomUser(roles = {"ADMIN"})
  void rateOrderTests(String url, String ratingRequest, HttpStatus expectedStatus)
      throws Exception {
    performPost(url, ratingRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideAdminRateOrderTestData() {
    String validRatingRequest = RatingRequestTestDataBuilder.builder().build().toJson();
    String invalidRatingRequest = "{}";
    return Stream.of(
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_COMPLETED_STATUS_ID + "/rating",
            validRatingRequest,
            HttpStatus.NO_CONTENT),
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_COMPLETED_STATUS_ID + "/rating",
            invalidRatingRequest,
            HttpStatus.BAD_REQUEST),
        Arguments.of(
            BASE_URL + "/" + INVALID_ORDER_ID + "/rating",
            validRatingRequest,
            HttpStatus.NOT_FOUND),
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_NOT_APPROVED_STATUS_ID + "/rating",
            validRatingRequest,
            HttpStatus.CONFLICT),
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_RATING_ID + "/rating",
            validRatingRequest,
            HttpStatus.CONFLICT));
  }

  @ParameterizedTest
  @MethodSource("provideUserRateOrderTestData")
  @DisplayName("POST /orders/{orderId}/rating (USER)")
  @WithCustomUser(id = VALID_USER_ID)
  void userRateOrderTests(String url, String ratingRequest, HttpStatus expectedStatus)
      throws Exception {
    performPost(url, ratingRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUserRateOrderTestData() {
    String validRatingRequest = RatingRequestTestDataBuilder.builder().build().toJson();
    return Stream.of(
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_COMPLETED_STATUS_ID + "/rating",
            validRatingRequest,
            HttpStatus.NO_CONTENT),
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_DIFFERENT_OWNER_ID + "/rating",
            validRatingRequest,
            HttpStatus.FORBIDDEN));
  }

  @ParameterizedTest
  @MethodSource("provideDeleteOrderTestData")
  @DisplayName("DELETE /orders/{orderId}")
  @WithCustomUser(roles = {"ADMIN"})
  void deleteOrderTests(String url, HttpStatus expectedStatus) throws Exception {
    performDelete(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideDeleteOrderTestData() {
    return Stream.of(
        Arguments.of(
            BASE_URL + "/" + VALID_ORDER_WITH_NOT_APPROVED_STATUS_ID, HttpStatus.NO_CONTENT),
        Arguments.of(BASE_URL + "/" + INVALID_ORDER_ID, HttpStatus.NOT_FOUND));
  }
}
