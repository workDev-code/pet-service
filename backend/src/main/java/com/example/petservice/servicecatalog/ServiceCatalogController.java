package com.example.petservice.servicecatalog;

import com.example.petservice.servicecatalog.ServiceCatalogDtos.ServiceCatalogResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class ServiceCatalogController {
  private final ServiceCatalogService serviceCatalogService;

  public ServiceCatalogController(ServiceCatalogService serviceCatalogService) {
    this.serviceCatalogService = serviceCatalogService;
  }

  @GetMapping
  List<ServiceCatalogResponse> list() {
    return serviceCatalogService.activeServices();
  }
}
