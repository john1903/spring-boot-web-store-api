package me.jangluzniewicz.webstore.categories.models;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Category {
    private Long id;
    @NonNull
    private String name;
}
