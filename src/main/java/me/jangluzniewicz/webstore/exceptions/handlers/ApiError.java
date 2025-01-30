package me.jangluzniewicz.webstore.exceptions.handlers;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApiError {
    private String status;
    private Integer code;
    private String detail;
    private String path;
    private LocalDateTime dateTime;
    private List<ErrorDetail> errors;
}
