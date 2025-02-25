package me.jangluzniewicz.webstore.categories.integrations.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import me.jangluzniewicz.webstore.utils.testdata.categories.CategoryRequestTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class CategoryControllerTest extends IntegrationTest {
  private static final String BASE_URL = "/categories";
  private static final Long VALID_CATEGORY_ID = 2L;
  private static final Long INVALID_CATEGORY_ID = 999L;

  @ParameterizedTest
  @MethodSource("provideGetCategoryTestData")
  @DisplayName("GET /categories")
  void getCategoryTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideGetCategoryTestData() {
    return Stream.of(
        Arguments.of(BASE_URL, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + VALID_CATEGORY_ID, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + INVALID_CATEGORY_ID, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideCreateCategoryTestData")
  @DisplayName("POST /categories")
  @WithCustomUser(roles = {"ADMIN"})
  void createCategoryTests(String categoryRequest, HttpStatus expectedStatus) throws Exception {
    performPost(BASE_URL, categoryRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideCreateCategoryTestData() {
    String validCategoryRequest =
        CategoryRequestTestDataBuilder.builder().name("NEW_CATEGORY").build().toJson();
    String invalidCategoryRequest = "{}";
    String duplicateCategoryRequest = CategoryRequestTestDataBuilder.builder().build().toJson();

    return Stream.of(
        Arguments.of(validCategoryRequest, HttpStatus.CREATED),
        Arguments.of(invalidCategoryRequest, HttpStatus.BAD_REQUEST),
        Arguments.of(duplicateCategoryRequest, HttpStatus.CONFLICT));
  }

  @ParameterizedTest
  @MethodSource("provideUpdateCategoryTestData")
  @DisplayName("PUT /categories")
  @WithCustomUser(roles = {"ADMIN"})
  void updateCategoryTests(String url, String categoryRequest, HttpStatus expectedStatus)
      throws Exception {
    performPut(url, categoryRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUpdateCategoryTestData() {
    String validUpdateRequest =
        CategoryRequestTestDataBuilder.builder().name("UPDATED").build().toJson();
    String invalidUpdateRequest = "{}";
    String duplicateUpdateRequest = CategoryRequestTestDataBuilder.builder().build().toJson();

    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_CATEGORY_ID, validUpdateRequest, HttpStatus.NO_CONTENT),
        Arguments.of(
            BASE_URL + "/" + VALID_CATEGORY_ID, invalidUpdateRequest, HttpStatus.BAD_REQUEST),
        Arguments.of(
            BASE_URL + "/" + INVALID_CATEGORY_ID, validUpdateRequest, HttpStatus.NOT_FOUND),
        Arguments.of(
            BASE_URL + "/" + VALID_CATEGORY_ID, duplicateUpdateRequest, HttpStatus.CONFLICT));
  }

  @ParameterizedTest
  @MethodSource("provideDeleteCategoryTestData")
  @DisplayName("DELETE /categories")
  @WithCustomUser(roles = {"ADMIN"})
  void deleteCategoryTests(String url, HttpStatus expectedStatus) throws Exception {
    performDelete(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideDeleteCategoryTestData() {
    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_CATEGORY_ID, HttpStatus.NO_CONTENT),
        Arguments.of(BASE_URL + "/" + INVALID_CATEGORY_ID, HttpStatus.NOT_FOUND));
  }
}
