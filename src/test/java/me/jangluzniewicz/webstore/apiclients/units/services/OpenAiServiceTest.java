package me.jangluzniewicz.webstore.apiclients.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.jangluzniewicz.webstore.apiclients.services.OpenAiService;
import me.jangluzniewicz.webstore.commons.testdata.products.ProductTestDataBuilder;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

@ExtendWith(MockitoExtension.class)
class OpenAiServiceTest {
  @Mock private IProduct productService;
  @Mock private ChatModel chatModel;
  @InjectMocks private OpenAiService openAiService;

  @Mock ChatResponse chatResponse;
  @Mock Generation generation;
  @Mock AssistantMessage assistantMessage;
  private Product product;

  @BeforeEach
  void setUp() {
    product = ProductTestDataBuilder.builder().build().buildProduct();
  }

  @Test
  void getProductSeoDescription_whenProductExists_thenReturnSeoDescriptionResponse() {
    String seoDescription = "SEO description";

    when(productService.getProductById(product.getId())).thenReturn(Optional.of(product));
    when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);
    when(chatResponse.getResult()).thenReturn(generation);
    when(generation.getOutput()).thenReturn(assistantMessage);
    when(assistantMessage.getText()).thenReturn(seoDescription);

    assertEquals(
        seoDescription, openAiService.getProductSeoDescription(product.getId()).getDescription());
  }

  @Test
  void getProductSeoDescription_whenProductDoesNotExist_thenThrowNotFoundException() {
    when(productService.getProductById(product.getId())).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> openAiService.getProductSeoDescription(product.getId()));
  }
}
