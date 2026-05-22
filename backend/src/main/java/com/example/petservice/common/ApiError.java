package com.example.petservice.common;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
    OffsetDateTime timestamp,
    int status,
    String error,
    String code,
    String message,
    List<FieldErrorDetail> fieldErrors
) {
  public static ApiError of(int status, String error, String message) {
    return of(status, error, "UNEXPECTED_ERROR", message);
  }

  public static ApiError of(int status, String error, String code, String message) {
    return new ApiError(OffsetDateTime.now(), status, error, code, message, List.of());
  }
}
