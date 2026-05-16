package com.example.petservice.servicecatalog;

import java.math.BigDecimal;

public final class ServiceCatalogDtos {
  private ServiceCatalogDtos() {}

  public record ServiceCatalogResponse(
      Long id,
      String name,
      String description,
      BigDecimal price,
      Integer durationMinutes,
      boolean active
  ) {}
}
