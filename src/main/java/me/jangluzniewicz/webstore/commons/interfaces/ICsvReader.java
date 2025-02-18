package me.jangluzniewicz.webstore.commons.interfaces;

import jakarta.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.List;

public interface ICsvReader<T> {
  List<T> csvToModel(@NotNull InputStream inputStream);
}
