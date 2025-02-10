package me.jangluzniewicz.webstore.common.models;

import java.util.List;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class PagedResponse<T> {
  @NonNull private Integer totalPages;
  @NonNull private List<T> content;
}
