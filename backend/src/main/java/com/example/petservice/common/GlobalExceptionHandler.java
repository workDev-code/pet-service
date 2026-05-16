package com.example.petservice.common;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
  ResponseEntity<ApiError> handleForbidden(Exception ex) {
    return build(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
    List<FieldErrorDetail> fields = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
        .toList();
    ApiError body = new ApiError(
        java.time.OffsetDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        "Validation failed",
        fields
    );
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> handleUnexpected(Exception ex) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(ApiError.of(status.value(), status.getReasonPhrase(), message));
  }
}
