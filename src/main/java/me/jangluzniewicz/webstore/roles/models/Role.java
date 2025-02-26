package me.jangluzniewicz.webstore.roles.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Model representing a role")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Role {
  @Schema(description = "Unique identifier of the role", example = "1")
  private Long id;

  @NonNull
  @Schema(description = "Name of the role", example = "ADMIN")
  private String name;
}
