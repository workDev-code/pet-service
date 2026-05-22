package com.example.petservice.booking;

import com.example.petservice.common.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidScheduledAtException extends BusinessException {
  public InvalidScheduledAtException() {
    super(
        HttpStatus.BAD_REQUEST,
        "INVALID_SCHEDULED_AT",
        "scheduledAt must be a future date-time in yyyy-MM-dd'T'HH:mm:ss or ISO offset format"
    );
  }
}
