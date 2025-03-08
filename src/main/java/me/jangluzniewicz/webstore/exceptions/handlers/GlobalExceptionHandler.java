package me.jangluzniewicz.webstore.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import me.jangluzniewicz.webstore.exceptions.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
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

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiDetailedError> handleConstraintViolationException(
      ConstraintViolationException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    List<ErrorDetail> errorDetails =
        e.getConstraintViolations().stream()
            .map(
                violation ->
                    ErrorDetail.builder()
                        .field(violation.getPropertyPath().toString())
                        .message(violation.getMessage())
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

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiDetailedError> handleHandlerMethodValidationException(
      HandlerMethodValidationException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    List<ErrorDetail> errorDetails =
        e.getAllErrors().stream()
            .map(
                objectError -> {
                  String field = null;
                  if (objectError instanceof FieldError) {
                    field = ((FieldError) objectError).getField();
                  }
                  return ErrorDetail.builder()
                      .field(field)
                      .message(objectError.getDefaultMessage())
                      .build();
                })
            .toList();
    ApiDetailedError apiError =
        ApiDetailedError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.name())
            .message(e.getReason())
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
  public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(
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

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDeniedException(
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

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiError> handleNoResourceFoundException(
      NoResourceFoundException e, HttpServletRequest request) {
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

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiError> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    ApiError apiError =
        ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.METHOD_NOT_ALLOWED.value())
            .error(HttpStatus.METHOD_NOT_ALLOWED.name())
            .message(e.getMessage())
            .path(url)
            .build();
    return new ResponseEntity<>(apiError, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> handleDataIntegrityViolationException(
      DataIntegrityViolationException e, HttpServletRequest request) {
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

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e, HttpServletRequest request) {
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

  @ExceptionHandler(OrderStatusNotAllowedException.class)
  public ResponseEntity<ApiError> handleOrderStatusNotAllowedException(
      OrderStatusNotAllowedException e, HttpServletRequest request) {
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

  @ExceptionHandler(CsvReaderException.class)
  public ResponseEntity<ApiError> handleCsvReaderException(
      CsvReaderException e, HttpServletRequest request) {
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

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<ApiError> handleMissingServletRequestPartException(
      MissingServletRequestPartException e, HttpServletRequest request) {
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

  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<ApiError> handleMultipartException(
      MultipartException e, HttpServletRequest request) {
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
