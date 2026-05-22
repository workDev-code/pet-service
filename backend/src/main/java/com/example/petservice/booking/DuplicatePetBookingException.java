package com.example.petservice.booking;

import com.example.petservice.common.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicatePetBookingException extends BusinessException {
  public DuplicatePetBookingException() {
    super(
        HttpStatus.CONFLICT,
        "DUPLICATE_PET_SLOT",
        "Pet already has a booking at this scheduled time"
    );
  }
}
