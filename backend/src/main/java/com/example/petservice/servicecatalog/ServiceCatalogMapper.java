package com.example.petservice.servicecatalog;

import com.example.petservice.servicecatalog.ServiceCatalogDtos.ServiceCatalogResponse;
import org.springframework.stereotype.Component;

@Component
public class ServiceCatalogMapper {
  public ServiceCatalogResponse toResponse(ServiceCatalog service) {
    return new ServiceCatalogResponse(
        service.getId(),
        service.getName(),
        service.getDescription(),
        service.getPrice(),
        service.getDurationMinutes(),
        service.isActive()
    );
  }
}
