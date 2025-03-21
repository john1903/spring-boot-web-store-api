package me.jangluzniewicz.webstore.carts.e2e.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.e2e.config.E2ETest;
import me.jangluzniewicz.webstore.utils.e2e.security.WithCustomUser;
import me.jangluzniewicz.webstore.utils.testdata.carts.CartItemRequestTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.carts.CartRequestTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class CartControllerTest extends E2ETest {
  private static final String BASE_URL = "/carts";
  private static final long VALID_USER_ID = 2L;
  private static final long INVALID_PRODUCT_ID = 999L;

  @ParameterizedTest
  @MethodSource("provideGetCartTestData")
  @DisplayName("GET /carts/current")
  @WithCustomUser(id = VALID_USER_ID)
  void getCartTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideGetCartTestData() {
    return Stream.of(Arguments.of(BASE_URL + "/current", HttpStatus.OK));
  }

  @ParameterizedTest
  @MethodSource("provideAddItemToCartTestData")
  @DisplayName("POST /carts/current/items")
  @WithCustomUser(id = VALID_USER_ID)
  void addItemToCartTests(String cartItemRequest, HttpStatus expectedStatus) throws Exception {
    performPost(BASE_URL + "/current/items", cartItemRequest)
        .andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideAddItemToCartTestData() {
    String validCartItemRequest = CartItemRequestTestDataBuilder.builder().build().toJson();
    String notFoundCartItemRequest =
        CartItemRequestTestDataBuilder.builder().productId(INVALID_PRODUCT_ID).build().toJson();
    return Stream.of(
        Arguments.of(validCartItemRequest, HttpStatus.NO_CONTENT),
        Arguments.of(notFoundCartItemRequest, HttpStatus.NOT_FOUND));
  }

  @ParameterizedTest
  @MethodSource("provideEmptyCartTestData")
  @DisplayName("DELETE /carts/current/items")
  @WithCustomUser(id = VALID_USER_ID)
  void emptyCartTests(String url, HttpStatus expectedStatus) throws Exception {
    performDelete(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideEmptyCartTestData() {
    return Stream.of(Arguments.of(BASE_URL + "/current/items", HttpStatus.NO_CONTENT));
  }

  @ParameterizedTest
  @MethodSource("provideUpdateCartTestData")
  @DisplayName("PUT /carts/current")
  @WithCustomUser(id = VALID_USER_ID)
  void updateCartTests(String cartRequest, HttpStatus expectedStatus) throws Exception {
    performPut(BASE_URL + "/current", cartRequest).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUpdateCartTestData() {
    String validCartRequest =
        CartRequestTestDataBuilder.builder()
            .items(List.of(CartItemRequestTestDataBuilder.builder().quantity(2).build()))
            .build()
            .toJson();
    String notFoundCartRequest =
        CartRequestTestDataBuilder.builder()
            .items(
                List.of(
                    CartItemRequestTestDataBuilder.builder().productId(INVALID_PRODUCT_ID).build()))
            .build()
            .toJson();
    return Stream.of(
        Arguments.of(validCartRequest, HttpStatus.NO_CONTENT),
        Arguments.of(notFoundCartRequest, HttpStatus.NOT_FOUND));
  }
}
