package me.jangluzniewicz.webstore.commons.models;

import java.util.List;
import lombok.*;

@AllArgsConstructor
@Getter
public class PagedResponse<T> {
  @NonNull private Integer totalPages;
  @NonNull private List<T> content;
}
