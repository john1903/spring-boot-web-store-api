package me.jangluzniewicz.webstore.commons.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Schema(description = "Response model containing an ID")
@AllArgsConstructor
@Getter
public class IdResponse {
  @NonNull
  @Schema(description = "Unique identifier", example = "1")
  private Long id;
}
