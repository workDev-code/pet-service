package com.example.petservice.servicecatalog;

import com.example.petservice.common.BusinessException;
import org.springframework.http.HttpStatus;

public class ServiceNotFoundException extends BusinessException {
  public ServiceNotFoundException() {
    super(HttpStatus.NOT_FOUND, "SERVICE_NOT_FOUND", "Active service not found");
  }
}
