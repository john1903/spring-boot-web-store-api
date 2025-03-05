package me.jangluzniewicz.webstore.openai.e2e.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.e2e.config.E2ETest;
import me.jangluzniewicz.webstore.utils.e2e.security.WithCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class OpenAiControllerTest extends E2ETest {
  private static final String BASE_URL = "/products";
  private static final long VALID_PRODUCT_ID = 1L;
  private static final long INVALID_PRODUCT_ID = 999L;

  @ParameterizedTest
  @MethodSource("provideGetOpenAiTestData")
  @DisplayName("GET /products/{productId}/seo-description")
  @WithCustomUser(roles = {"ADMIN"})
  void getOpenAiTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideGetOpenAiTestData() {
    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_PRODUCT_ID + "/seo-description", HttpStatus.OK),
        Arguments.of(
            BASE_URL + "/" + INVALID_PRODUCT_ID + "/seo-description", HttpStatus.NOT_FOUND));
  }
}
