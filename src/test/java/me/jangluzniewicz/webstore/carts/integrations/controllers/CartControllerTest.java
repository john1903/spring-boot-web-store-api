package me.jangluzniewicz.webstore.carts.integrations.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;
import me.jangluzniewicz.webstore.utils.integrations.config.IntegrationTest;
import me.jangluzniewicz.webstore.utils.integrations.security.WithCustomUser;
import me.jangluzniewicz.webstore.utils.testdata.carts.CartItemRequestTestDataBuilder;
import me.jangluzniewicz.webstore.utils.testdata.carts.CartRequestTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class CartControllerTest extends IntegrationTest {
  private static final String CART_CURRENT_URL = "/carts/current";
  private static final String CART_ITEMS_URL = CART_CURRENT_URL + "/items";
  private static final long VALID_USER_ID = 2;

  @ParameterizedTest
  @MethodSource("provideGetCartTestData")
  @DisplayName("GET /carts/current")
  @WithCustomUser(id = VALID_USER_ID)
  void getCartTests(String url, HttpStatus expectedStatus) throws Exception {
    performGet(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideGetCartTestData() {
    return Stream.of(
            Arguments.of(CART_CURRENT_URL, HttpStatus.OK)
    );
  }

  @ParameterizedTest
  @MethodSource("provideAddItemToCartTestData")
  @DisplayName("POST /carts/current/items")
  @WithCustomUser(id = VALID_USER_ID)
  void addItemToCartTests(String cartItemRequest, HttpStatus expectedStatus) throws Exception {
    performPost(CART_ITEMS_URL, cartItemRequest)
            .andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideAddItemToCartTestData() {
    String validCartItemRequest = CartItemRequestTestDataBuilder.builder().build().toJson();
    String notFoundCartItemRequest = CartItemRequestTestDataBuilder.builder().productId(999L).build().toJson();
    return Stream.of(
            Arguments.of(validCartItemRequest, HttpStatus.NO_CONTENT),
            Arguments.of(notFoundCartItemRequest, HttpStatus.NOT_FOUND)
    );
  }

  @ParameterizedTest
  @MethodSource("provideEmptyCartTestData")
  @DisplayName("DELETE /carts/current/items")
  @WithCustomUser(id = VALID_USER_ID)
  void emptyCartTests(String url, HttpStatus expectedStatus) throws Exception {
    performDelete(url).andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideEmptyCartTestData() {
    return Stream.of(
            Arguments.of(CART_ITEMS_URL, HttpStatus.NO_CONTENT)
    );
  }

  @ParameterizedTest
  @MethodSource("provideUpdateCartTestData")
  @DisplayName("PUT /carts/current")
  @WithCustomUser(id = VALID_USER_ID)
  void updateCartTests(String cartRequest, HttpStatus expectedStatus) throws Exception {
    performPut(CART_CURRENT_URL, cartRequest)
            .andExpect(status().is(expectedStatus.value()));
  }

  static Stream<Arguments> provideUpdateCartTestData() {
    String validCartRequest = CartRequestTestDataBuilder.builder()
            .items(List.of(CartItemRequestTestDataBuilder.builder().quantity(2).build()))
            .build()
            .toJson();
    String notFoundCartRequest = CartRequestTestDataBuilder.builder()
            .items(List.of(CartItemRequestTestDataBuilder.builder().productId(999L).build()))
            .build()
            .toJson();
    return Stream.of(
            Arguments.of(validCartRequest, HttpStatus.NO_CONTENT),
            Arguments.of(notFoundCartRequest, HttpStatus.NOT_FOUND)
    );
  }
}