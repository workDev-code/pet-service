package com.example.petservice.pet;

import com.example.petservice.common.BusinessException;
import org.springframework.http.HttpStatus;

public class PetNotFoundException extends BusinessException {
  public PetNotFoundException() {
    super(HttpStatus.NOT_FOUND, "PET_NOT_FOUND", "Pet not found");
  }
}
