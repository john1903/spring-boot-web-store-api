package me.jangluzniewicz.webstore.common.models;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PagedResponse <T> {
    @NonNull
    private Integer totalPages;
    @NonNull
    private List<T> content;
}
