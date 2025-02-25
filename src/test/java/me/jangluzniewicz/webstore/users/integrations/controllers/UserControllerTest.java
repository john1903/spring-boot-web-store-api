package me.jangluzniewicz.webstore.users.integrations.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import me.jangluzniewicz.webstore.utils.testdata.users.UserRequestTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class UserControllerTest extends IntegrationTest {
  private static final String BASE_URL = "/users";
  private static final long VALID_USER_WITH_ROLE_ADMIN = 1L;
  private static final long VALID_USER_WITH_ROLE_CUSTOMER = 2L;
  private static final long INVALID_USER_ID = 999L;

  @ParameterizedTest
  @MethodSource("provideAdminGetUsersTestData")
  @DisplayName("GET /users (ADMIN)")
  @WithCustomUser(roles = {"ADMIN"})
  void adminGetUsersTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideAdminGetUsersTestData() {
    return Stream.of(
        Arguments.of(BASE_URL, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + INVALID_USER_ID, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideAdminUpdateUsersTestData")
  @DisplayName("PUT /users (ADMIN)")
  @WithCustomUser(roles = {"ADMIN"})
  void adminUpdateUsersTests(String url, String userRequest, HttpStatus expectedStatus)
      throws Exception {
    performPut(url, userRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideAdminUpdateUsersTestData() {
    String validRoleUpdateRequest =
        UserRequestTestDataBuilder.builder().roleId(1L).build().toJson();
    String invalidRequest = "{}";
    String defaultRequest = UserRequestTestDataBuilder.builder().build().toJson();
    return Stream.of(
        Arguments.of(
            BASE_URL + "/" + VALID_USER_WITH_ROLE_CUSTOMER,
            validRoleUpdateRequest,
            HttpStatus.NO_CONTENT),
        Arguments.of(
            BASE_URL + "/" + VALID_USER_WITH_ROLE_CUSTOMER, invalidRequest, HttpStatus.BAD_REQUEST),
        Arguments.of(BASE_URL + "/" + INVALID_USER_ID, defaultRequest, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideAdminDeleteUsersTestData")
  @DisplayName("DELETE /users (ADMIN)")
  @WithCustomUser(roles = {"ADMIN"})
  void adminDeleteUsersTests(String url, HttpStatus expectedStatus) throws Exception {
    performDelete(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideAdminDeleteUsersTestData() {
    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_USER_WITH_ROLE_CUSTOMER, HttpStatus.NO_CONTENT),
        Arguments.of(BASE_URL + "/" + INVALID_USER_ID, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideUserGetTests")
  @DisplayName("GET /users (USER)")
  @WithCustomUser(id = VALID_USER_WITH_ROLE_CUSTOMER)
  void userGetTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUserGetTests() {
    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_USER_WITH_ROLE_CUSTOMER, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + VALID_USER_WITH_ROLE_ADMIN, HttpStatus.FORBIDDEN));
  }

  @ParameterizedTest
  @MethodSource("provideUserUpdateTests")
  @DisplayName("PUT /users (USER)")
  @WithCustomUser(id = VALID_USER_WITH_ROLE_CUSTOMER)
  void userUpdateTests(String url, String userRequest, HttpStatus expectedStatus) throws Exception {
    performPut(url, userRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUserUpdateTests() {
    String validUpdateRequest =
        UserRequestTestDataBuilder.builder().phoneNumber("123456789").build().toJson();
    String roleUpdateRequest = UserRequestTestDataBuilder.builder().roleId(1L).build().toJson();
    String defaultRequest = UserRequestTestDataBuilder.builder().build().toJson();
    return Stream.of(
        Arguments.of(
            BASE_URL + "/" + VALID_USER_WITH_ROLE_CUSTOMER,
            validUpdateRequest,
            HttpStatus.NO_CONTENT),
        Arguments.of(
            BASE_URL + "/" + VALID_USER_WITH_ROLE_CUSTOMER,
            roleUpdateRequest,
            HttpStatus.FORBIDDEN),
        Arguments.of(
            BASE_URL + "/" + VALID_USER_WITH_ROLE_ADMIN, defaultRequest, HttpStatus.FORBIDDEN));
  }
}
