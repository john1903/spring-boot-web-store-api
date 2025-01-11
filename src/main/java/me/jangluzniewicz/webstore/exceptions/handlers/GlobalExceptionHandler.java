package me.jangluzniewicz.webstore.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import me.jangluzniewicz.webstore.exceptions.DeletionNotAllowedException;
import me.jangluzniewicz.webstore.exceptions.IdViolationException;
import me.jangluzniewicz.webstore.exceptions.NotFoundException;
import me.jangluzniewicz.webstore.exceptions.NotUniqueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .detail(ex.getMessage())
                .path(url)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IdViolationException.class)
    public ResponseEntity<ApiError> handleIdViolationException(IdViolationException ex, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .detail(ex.getMessage())
                .path(url)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotUniqueException.class)
    public ResponseEntity<ApiError> handleNotUniqueException(NotUniqueException ex, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .detail(ex.getMessage())
                .path(url)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DeletionNotAllowedException.class)
    public ResponseEntity<ApiError> handleDeletionNotAllowedException(DeletionNotAllowedException ex, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .detail(ex.getMessage())
                .path(url)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        List<ErrorDetail> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ErrorDetail.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .toList();
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .detail("Validation failed")
                .path(url)
                .timestamp(LocalDateTime.now())
                .errors(errorDetails)
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}