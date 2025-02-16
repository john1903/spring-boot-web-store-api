package me.jangluzniewicz.webstore.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class IdResponse {
  @NonNull private Long id;
}
