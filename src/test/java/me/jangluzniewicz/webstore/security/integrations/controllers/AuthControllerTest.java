package me.jangluzniewicz.webstore.security.integrations.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import me.jangluzniewicz.webstore.utils.testdata.security.LoginRequestTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.users.UserRequestTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class AuthControllerTest extends IntegrationTest {
  private static final String LOGIN_URL = "/auth/login";
  private static final String SIGNUP_URL = "/auth/signup";

  @ParameterizedTest
  @MethodSource("provideLoginTestData")
  @DisplayName("POST /auth/login")
  void loginTests(String loginRequest, HttpStatus expectedStatus) throws Exception {
    performPost(LOGIN_URL, loginRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideLoginTestData() {
    String validLogin = LoginRequestTestDataBuilder.builder().build().toJson();
    String nonExistentUserLogin =
        LoginRequestTestDataBuilder.builder().username("new@new.com").build().toJson();
    String invalidLogin = "{}";

    return Stream.of(
        Arguments.of(validLogin, HttpStatus.OK),
        Arguments.of(nonExistentUserLogin, HttpStatus.UNAUTHORIZED),
        Arguments.of(invalidLogin, HttpStatus.UNAUTHORIZED));
  }

  @ParameterizedTest
  @MethodSource("provideCreateUserTestData")
  @DisplayName("POST /auth/signup - (USER)")
  void createUserTests(String userRequest, HttpStatus expectedStatus) throws Exception {
    performPost(SIGNUP_URL, userRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideCreateUserTestData() {
    String validUser = UserRequestTestDataBuilder.builder().email("new@new.com").build().toJson();
    String emailExists = UserRequestTestDataBuilder.builder().build().toJson();
    String userAdmin =
        UserRequestTestDataBuilder.builder().email("new@new.com").roleId(1L).build().toJson();
    String invalidUser = "{}";

    return Stream.of(
        Arguments.of(validUser, HttpStatus.CREATED),
        Arguments.of(emailExists, HttpStatus.CONFLICT),
        Arguments.of(userAdmin, HttpStatus.FORBIDDEN),
        Arguments.of(invalidUser, HttpStatus.BAD_REQUEST));
  }

  @ParameterizedTest
  @MethodSource("provideCreateUserAdminTestData")
  @DisplayName("POST /auth/signup - (ADMIN)")
  @WithCustomUser(roles = {"ADMIN"})
  void createUserAdminTests(String userRequest, HttpStatus expectedStatus) throws Exception {
    performPost(SIGNUP_URL, userRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideCreateUserAdminTestData() {
    String validUserAdmin =
        UserRequestTestDataBuilder.builder().email("new@new.com").roleId(1L).build().toJson();

    return Stream.of(Arguments.of(validUserAdmin, HttpStatus.CREATED));
  }
}
