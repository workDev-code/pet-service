package com.example.petservice.booking;

import com.example.petservice.common.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateServiceSlotException extends BusinessException {
  public DuplicateServiceSlotException() {
    super(
        HttpStatus.CONFLICT,
        "DUPLICATE_SERVICE_SLOT",
        "Service already has a booking at this scheduled time"
    );
  }
}
