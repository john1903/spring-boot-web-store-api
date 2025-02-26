package me.jangluzniewicz.webstore.commons.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.*;

@Schema(description = "Paginated response containing a list of items")
@AllArgsConstructor
@Getter
public class PagedResponse<T> {
  @NonNull
  @Schema(description = "Total number of pages", example = "5")
  private Integer totalPages;

  @NonNull
  @Schema(description = "List of items on the current page")
  private List<T> content;
}
