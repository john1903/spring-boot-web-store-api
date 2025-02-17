package me.jangluzniewicz.webstore.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class IdResponse {
  @NonNull private Long id;
}
