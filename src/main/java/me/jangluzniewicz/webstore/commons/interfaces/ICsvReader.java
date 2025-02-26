package me.jangluzniewicz.webstore.commons.interfaces;

import jakarta.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.List;

/**
 * Interface for reading CSV data and converting it to model objects.
 *
 * @param <T> the type of the model objects.
 */
public interface ICsvReader<T> {

  /**
   * Converts CSV data from an input stream to a list of model objects.
   *
   * @param inputStream the input stream containing the CSV data; must not be null.
   * @return a list of model objects parsed from the CSV data.
   */
  List<T> csvToModel(@NotNull InputStream inputStream);
}
