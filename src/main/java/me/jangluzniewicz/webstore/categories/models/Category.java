package me.jangluzniewicz.webstore.categories.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Model representing a category")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Category {
  @Schema(description = "Unique identifier of the category", example = "1")
  private Long id;

  @JsonIgnore
  @Schema(description = "Image URI of the category")
  private String imageUri;

  @Schema(description = "Image URL of the category")
  private String imageUrl;

  @NonNull
  @Schema(description = "Name of the category", example = "Bicycles")
  private String name;
}
