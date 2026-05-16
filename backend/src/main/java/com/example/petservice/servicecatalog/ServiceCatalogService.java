package com.example.petservice.servicecatalog;

import com.example.petservice.servicecatalog.ServiceCatalogDtos.ServiceCatalogResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceCatalogService {
  private final ServiceCatalogRepository services;
  private final ServiceCatalogMapper mapper;

  public ServiceCatalogService(ServiceCatalogRepository services, ServiceCatalogMapper mapper) {
    this.services = services;
    this.mapper = mapper;
  }

  @Transactional(readOnly = true)
  public List<ServiceCatalogResponse> activeServices() {
    return services.findByActiveTrueOrderByNameAsc().stream().map(mapper::toResponse).toList();
  }
}
