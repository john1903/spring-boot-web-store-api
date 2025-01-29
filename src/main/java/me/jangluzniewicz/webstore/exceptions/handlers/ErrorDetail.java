package me.jangluzniewicz.webstore.exceptions.handlers;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
class ErrorDetail {
    private String field;
    private String message;
}