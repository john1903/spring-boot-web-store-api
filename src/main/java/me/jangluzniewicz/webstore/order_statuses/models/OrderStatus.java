package me.jangluzniewicz.webstore.order_statuses.models;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class OrderStatus {
  private Long id;
  @NonNull private String name;
}
