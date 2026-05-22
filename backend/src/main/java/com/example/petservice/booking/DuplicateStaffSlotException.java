package com.example.petservice.booking;

import com.example.petservice.common.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateStaffSlotException extends BusinessException {
  public DuplicateStaffSlotException() {
    super(
        HttpStatus.CONFLICT,
        "DUPLICATE_STAFF_SLOT",
        "Staff already has a booking at this scheduled time"
    );
  }
}
