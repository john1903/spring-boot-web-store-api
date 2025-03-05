package me.jangluzniewicz.webstore.utils.e2e.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ProfilesInitializer.class)
@Tag("e2e")
@Transactional
public abstract class E2ETest {
  @Autowired private MockMvc mockMvc;

  protected ResultActions performGet(String url) throws Exception {
    return mockMvc.perform(get(url));
  }

  protected ResultActions performPost(String url, String content) throws Exception {
    return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(content));
  }

  protected ResultActions performPut(String url, String content) throws Exception {
    return mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(content));
  }

  protected ResultActions performDelete(String url) throws Exception {
    return mockMvc.perform(delete(url));
  }

  protected ResultActions performMultipart(String url, MockMultipartFile file) throws Exception {
    return mockMvc.perform(multipart(url).file(file));
  }
}
