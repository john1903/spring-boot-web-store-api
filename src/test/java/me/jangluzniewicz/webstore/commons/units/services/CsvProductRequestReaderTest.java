package me.jangluzniewicz.webstore.commons.units.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import me.jangluzniewicz.webstore.commons.services.CsvProductRequestReader;
import me.jangluzniewicz.webstore.exceptions.CsvReaderException;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import me.jangluzniewicz.webstore.utils.units.config.UnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class CsvProductRequestReaderTest extends UnitTest {
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

  @Test
  void csvToModel_whenEmptyCsvGiven_thenThrowCsvReaderException() {
    String csv = "";
    InputStream inputStream = new ByteArrayInputStream(csv.getBytes());

    assertThrows(CsvReaderException.class, () -> csvProductRequestReader.csvToModel(inputStream));
  }

  @Test
  void csvToModel_whenToFewColumnsInRow_thenThrowCsvReaderException() {
    String csv = "Headphones,Best sound quality,1200.0,0.2";
    InputStream inputStream = new ByteArrayInputStream(csv.getBytes());

    assertThrows(CsvReaderException.class, () -> csvProductRequestReader.csvToModel(inputStream));
  }

  @Test
  void csvToModel_whenInvalidNumberFormat_thenThrowCsvReaderException() {
    String csv = "Headphones,Best sound quality,1200.0,0.2,invalid";
    InputStream inputStream = new ByteArrayInputStream(csv.getBytes());

    assertThrows(CsvReaderException.class, () -> csvProductRequestReader.csvToModel(inputStream));
  }

  @Test
  void csvToModel_whenInvalidProduct_thenThrowCsvReaderException() {
    String csv = "Headphones,Best sound quality,1200.0,0.2,1";
    InputStream inputStream = new ByteArrayInputStream(csv.getBytes());
    @SuppressWarnings("unchecked")
    ConstraintViolation<ProductRequest> violation =
        (ConstraintViolation<ProductRequest>) mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("Invalid product");
    when(validator.validate(any(ProductRequest.class))).thenReturn(Set.of(violation));

    assertThrows(CsvReaderException.class, () -> csvProductRequestReader.csvToModel(inputStream));
  }
}
