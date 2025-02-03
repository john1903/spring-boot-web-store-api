package me.jangluzniewicz.webstore.roles.models;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Role {
  private Long id;
  @NonNull private String name;
}
