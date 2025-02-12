package me.jangluzniewicz.webstore.exceptions.handlers;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ApiError {
  private LocalDateTime timestamp;
  private Integer status;
  private String error;
  private String message;
  private String path;
}
