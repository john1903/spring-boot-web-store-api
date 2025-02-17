package me.jangluzniewicz.webstore.apiclients.services;

import me.jangluzniewicz.webstore.apiclients.interfaces.IOpenAi;
import me.jangluzniewicz.webstore.apiclients.models.SeoDescriptionResponse;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.products.interfaces.IProduct;
import me.jangluzniewicz.webstore.products.models.Product;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService implements IOpenAi {
  private final ChatModel chatModel;
  private final IProduct productService;

  public OpenAiService(IProduct productService, ChatModel chatModel) {
    this.productService = productService;
    this.chatModel = chatModel;
  }

  @Override
  public SeoDescriptionResponse getProductSeoDescription(Long productId) {
    Product product =
        productService
            .getProductById(productId)
            .orElseThrow(
                () -> new NotFoundException("Product with id " + productId + " not found"));
    String prompt = "Generate SEO description for product: " + product.toString();
    String response =
        chatModel
            .call(new Prompt(prompt, OpenAiChatOptions.builder().model("gpt-4o-mini").build()))
            .getResult()
            .getOutput()
            .getText();
    return new SeoDescriptionResponse(response);
  }
}
