package me.jangluzniewicz.webstore.commons.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.jangluzniewicz.webstore.commons.interfaces.ICsvReader;
import me.jangluzniewicz.webstore.exceptions.CsvReaderException;
import me.jangluzniewicz.webstore.products.controllers.ProductRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CsvProductRequestReader implements ICsvReader<ProductRequest> {
  private final Validator validator;

  public CsvProductRequestReader(Validator validator) {
    this.validator = validator;
  }

  @Override
  public List<ProductRequest> csvToModel(InputStream inputStream) {
    List<ProductRequest> productRequests = new ArrayList<>();
    try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
      List<String[]> rows = reader.readAll();
      if (rows.isEmpty()) {
        throw new CsvReaderException("Empty CSV file");
      }
      for (String[] row : rows) {
        if (row.length < 5) {
          throw new CsvReaderException("To few columns in row: " + String.join(",", row));
        }
        try {
          ProductRequest productRequest =
              new ProductRequest(
                  null,
                  row[0],
                  row[1],
                  BigDecimal.valueOf(Double.parseDouble(row[2])),
                  BigDecimal.valueOf(Double.parseDouble(row[3])),
                  Long.parseLong(row[4]));
          Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
          if (!violations.isEmpty()) {
            String message =
                violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new CsvReaderException(
                "Validation errors in row: " + String.join(",", row) + " - " + message);
          }
          productRequests.add(productRequest);
        } catch (NumberFormatException e) {
          throw new CsvReaderException("Error while parsing row: " + String.join(",", row));
        }
      }
    } catch (CsvException | IOException e) {
      throw new CsvReaderException("Error while reading CSV file");
    }
    return productRequests;
  }
}
