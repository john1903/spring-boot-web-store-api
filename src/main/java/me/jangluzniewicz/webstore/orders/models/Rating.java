package me.jangluzniewicz.webstore.orders.models;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Rating {
    private Long id;
    @NonNull
    private Integer rating;
    @NonNull
    private String description;
}
