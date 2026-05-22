package com.example.petservice.common;

import java.util.List;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BusinessException.class)
  ResponseEntity<ApiError> handleBusiness(BusinessException ex) {
    return build(ex.getStatus(), ex.getCode(), ex.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
  }

  @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
  ResponseEntity<ApiError> handleForbidden(Exception ex) {
    return build(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
    return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  ResponseEntity<ApiError> handleUnauthorized(AuthenticationException ex) {
    return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid email or password");
  }

  @ExceptionHandler({
      MaxUploadSizeExceededException.class,
      MissingServletRequestParameterException.class,
      MissingServletRequestPartException.class,
      MultipartException.class
  })
  ResponseEntity<ApiError> handleMultipart(Exception ex) {
    return build(HttpStatus.BAD_REQUEST, "INVALID_UPLOAD_REQUEST", "Invalid upload request");
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
        "VALIDATION_FAILED",
        "Validation failed",
        fields
    );
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException ex) {
    return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_BODY", "Request body is malformed or has invalid data types");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
    String message = ex.getMostSpecificCause().getMessage();
    if (message != null) {
      if (message.contains("ux_bookings_pet_scheduled_active")) {
        return build(HttpStatus.CONFLICT, "DUPLICATE_PET_SLOT", "Pet already has a booking at this scheduled time");
      }
      if (message.contains("ux_bookings_service_scheduled_active")) {
        return build(HttpStatus.CONFLICT, "DUPLICATE_SERVICE_SLOT", "Service already has a booking at this scheduled time");
      }
      if (message.contains("ux_bookings_staff_scheduled_active")) {
        return build(HttpStatus.CONFLICT, "DUPLICATE_STAFF_SLOT", "Staff already has a booking at this scheduled time");
      }
    }
    return build(HttpStatus.CONFLICT, "DATA_INTEGRITY_CONFLICT", "Request conflicts with existing data");
  }

  @ExceptionHandler({
      ObjectOptimisticLockingFailureException.class,
      OptimisticLockException.class,
      CannotAcquireLockException.class
  })
  ResponseEntity<ApiError> handleConcurrency(Exception ex) {
    return build(HttpStatus.CONFLICT, "CONCURRENT_BOOKING_UPDATE", "Booking was updated by another request");
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> handleUnexpected(Exception ex) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR", "Unexpected server error");
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String message) {
    return build(status, status.name(), message);
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String code, String message) {
    return ResponseEntity.status(status)
        .body(ApiError.of(status.value(), status.getReasonPhrase(), code, message));
  }
}
