package me.jangluzniewicz.webstore.commons.services.units;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import me.jangluzniewicz.webstore.commons.services.CsvProductRequestReader;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsvProductRequestReaderTest {
  @Mock Validator validator;
  @InjectMocks CsvProductRequestReader csvProductRequestReader;

  @Test
  void csvToModel_whenValidCsvGiven_thenReturnProductRequestList() {
    String csv =
        """
        Headphones,Best sound quality,1200.0,0.2,1
        Keyboard,RGB backlight,200.0,0.5,1
        """;
    InputStream inputStream = new ByteArrayInputStream(csv.getBytes());
    when(validator.validate(any(ProductRequest.class))).thenReturn(Set.of());

    List<ProductRequest> productRequests = csvProductRequestReader.csvToModel(inputStream);
    assertEquals(2, productRequests.size());
  }
}
