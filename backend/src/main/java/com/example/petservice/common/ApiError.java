package com.example.petservice.common;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
    OffsetDateTime timestamp,
    int status,
    String error,
    String message,
    List<FieldErrorDetail> fieldErrors
) {
  public static ApiError of(int status, String error, String message) {
    return new ApiError(OffsetDateTime.now(), status, error, message, List.of());
  }
}
