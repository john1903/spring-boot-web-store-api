package me.jangluzniewicz.webstore.roles.integrations.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import me.jangluzniewicz.webstore.utils.testdata.roles.RoleRequestTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class RoleControllerTest extends IntegrationTest {
  private static final String BASE_URL = "/roles";
  private static final Long VALID_ROLE_ID = 1L;
  private static final Long INVALID_ROLE_ID = 999L;

  @ParameterizedTest
  @MethodSource("provideGetRoleTestData")
  @DisplayName("GET /roles")
  void getRoleTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideGetRoleTestData() {
    return Stream.of(
        Arguments.of(BASE_URL, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + VALID_ROLE_ID, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + INVALID_ROLE_ID, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideCreateRoleTestData")
  @DisplayName("POST /roles")
  @WithCustomUser(roles = {"ADMIN"})
  void createRoleTests(String roleRequest, HttpStatus expectedStatus) throws Exception {
    performPost(BASE_URL, roleRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideCreateRoleTestData() {
    String validRoleRequest = RoleRequestTestDataBuilder.builder().name("TEST").build().toJson();
    String duplicateRoleRequest = RoleRequestTestDataBuilder.builder().build().toJson();
    String invalidRoleRequest = "{}";

    return Stream.of(
        Arguments.of(validRoleRequest, HttpStatus.CREATED),
        Arguments.of(duplicateRoleRequest, HttpStatus.CONFLICT),
        Arguments.of(invalidRoleRequest, HttpStatus.BAD_REQUEST));
  }

  @ParameterizedTest
  @MethodSource("provideUpdateRoleTestData")
  @DisplayName("PUT /roles")
  @WithCustomUser(roles = {"ADMIN"})
  void updateRoleTests(String url, String roleRequest, HttpStatus expectedStatus) throws Exception {
    performPut(url, roleRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUpdateRoleTestData() {
    String validRoleRequest = RoleRequestTestDataBuilder.builder().name("TEST").build().toJson();
    String duplicateRoleRequest = RoleRequestTestDataBuilder.builder().build().toJson();

    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_ROLE_ID, validRoleRequest, HttpStatus.NO_CONTENT),
        Arguments.of(BASE_URL + "/" + VALID_ROLE_ID, duplicateRoleRequest, HttpStatus.CONFLICT),
        Arguments.of(BASE_URL + "/" + INVALID_ROLE_ID, validRoleRequest, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideDeleteRoleTestData")
  @DisplayName("DELETE /roles")
  @WithCustomUser(roles = {"ADMIN"})
  void deleteRoleTests(String url, HttpStatus expectedStatus) throws Exception {
    performDelete(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideDeleteRoleTestData() {
    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_ROLE_ID, HttpStatus.NO_CONTENT),
        Arguments.of(BASE_URL + "/" + INVALID_ROLE_ID, HttpStatus.NOT_FOUND));
  }
}
