package me.jangluzniewicz.webstore.exceptions.handlers;

import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ApiDetailedError extends ApiError {
  private List<ErrorDetail> details;
}
