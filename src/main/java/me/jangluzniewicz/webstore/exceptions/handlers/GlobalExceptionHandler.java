package me.jangluzniewicz.webstore.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import me.jangluzniewicz.webstore.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFoundException(
      NotFoundException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    ApiError apiError =
        ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.name())
            .message(e.getMessage())
            .path(url)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(NotUniqueException.class)
  public ResponseEntity<ApiError> handleNotUniqueException(
      NotUniqueException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    ApiError apiError =
        ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error(HttpStatus.CONFLICT.name())
            .message(e.getMessage())
            .path(url)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(DeletionNotAllowedException.class)
  public ResponseEntity<ApiError> handleDeletionNotAllowedException(
      DeletionNotAllowedException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    ApiError apiError =
        ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error(HttpStatus.CONFLICT.name())
            .message(e.getMessage())
            .path(url)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiDetailedError> handleValidationExceptions(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    List<ErrorDetail> errorDetails =
        e.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    ErrorDetail.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
            .toList();
    ApiDetailedError apiError =
        ApiDetailedError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.name())
            .message(e.getMessage())
            .path(url)
            .details(errorDetails)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiError> handleConflictException(
      ConflictException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    ApiError apiError =
        ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error(HttpStatus.CONFLICT.name())
            .message(e.getMessage())
            .path(url)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(
      IllegalArgumentException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    ApiError apiError =
        ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.name())
            .message(e.getMessage())
            .path(url)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleNumberFormatException(
      MethodArgumentTypeMismatchException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    ApiError apiError =
        ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.name())
            .message(
                e.getName()
                    + " should be of type "
                    + (e.getRequiredType() != null
                        ? e.getRequiredType().getSimpleName()
                        : "unknown"))
            .path(url)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ApiError> handleAuthorizationDeniedException(
      AuthorizationDeniedException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    ApiError apiError =
        ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error(HttpStatus.FORBIDDEN.name())
            .message(e.getMessage())
            .path(url)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleException(Exception e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    ApiError apiError =
        ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
            .message(e.getMessage())
            .path(url)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
