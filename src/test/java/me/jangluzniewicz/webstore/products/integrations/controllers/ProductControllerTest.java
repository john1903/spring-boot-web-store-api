package me.jangluzniewicz.webstore.products.integrations.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import me.jangluzniewicz.webstore.utils.testdata.products.ProductFilterRequestTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.products.ProductRequestTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

public class ProductControllerTest extends IntegrationTest {
  private static final String BASE_URL = "/products";
  private static final long VALID_PRODUCT_ID = 1L;
  private static final long INVALID_PRODUCT_ID = 999L;

  @ParameterizedTest
  @MethodSource("provideGetProductsTestData")
  @DisplayName("GET /products")
  void getProductsTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideGetProductsTestData() {
    String filterParams = ProductFilterRequestTestDataBuilder.builder().build().toRequestParams();
    return Stream.of(
        Arguments.of(BASE_URL, HttpStatus.OK),
        Arguments.of(BASE_URL + "?" + filterParams, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + VALID_PRODUCT_ID, HttpStatus.OK),
        Arguments.of(BASE_URL + "/" + INVALID_PRODUCT_ID, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideCreateProductTestData")
  @DisplayName("POST /products")
  @WithCustomUser(roles = {"ADMIN"})
  void createProductTests(String productRequest, HttpStatus expectedStatus) throws Exception {
    performPost(BASE_URL, productRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideCreateProductTestData() {
    String validProductRequest =
        ProductRequestTestDataBuilder.builder().name("NEW_PRODUCT").build().toJson();
    String invalidProductRequest = "{}";
    return Stream.of(
        Arguments.of(validProductRequest, HttpStatus.CREATED),
        Arguments.of(invalidProductRequest, HttpStatus.BAD_REQUEST));
  }

  @ParameterizedTest
  @MethodSource("provideCsvImportTestData")
  @DisplayName("POST /products/import/csv")
  @WithCustomUser(roles = {"ADMIN"})
  void createProductFromCsvTests(MockMultipartFile file, HttpStatus expectedStatus)
      throws Exception {
    performMultipart(BASE_URL + "/import/csv", file).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideCsvImportTestData() {
    String csvContent =
        """
        Headphones,Best sound quality,1200.0,0.2,1
        Keyboard,RGB backlight,200.0,0.5,1
        """;
    MockMultipartFile file =
        new MockMultipartFile(
            "file", "products.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));
    return Stream.of(Arguments.of(file, HttpStatus.NO_CONTENT));
  }

  @ParameterizedTest
  @MethodSource("provideUpdateProductTestData")
  @DisplayName("PUT /products")
  @WithCustomUser(roles = {"ADMIN"})
  void updateProductTests(String url, String productRequest, HttpStatus expectedStatus)
      throws Exception {
    performPut(url, productRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUpdateProductTestData() {
    String validProductRequest =
        ProductRequestTestDataBuilder.builder().name("UPDATED_PRODUCT").build().toJson();
    String invalidProductRequest = "{}";
    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_PRODUCT_ID, validProductRequest, HttpStatus.NO_CONTENT),
        Arguments.of(
            BASE_URL + "/" + VALID_PRODUCT_ID, invalidProductRequest, HttpStatus.BAD_REQUEST),
        Arguments.of(
            BASE_URL + "/" + INVALID_PRODUCT_ID, validProductRequest, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideDeleteProductTestData")
  @DisplayName("DELETE /products")
  @WithCustomUser(roles = {"ADMIN"})
  void deleteProductTests(String url, HttpStatus expectedStatus) throws Exception {
    performDelete(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideDeleteProductTestData() {
    return Stream.of(
        Arguments.of(BASE_URL + "/" + VALID_PRODUCT_ID, HttpStatus.NO_CONTENT),
        Arguments.of(BASE_URL + "/" + INVALID_PRODUCT_ID, HttpStatus.NOT_FOUND));
  }
}
