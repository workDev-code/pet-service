package com.example.petservice.servicecatalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {
  List<ServiceCatalog> findByActiveTrueOrderByNameAsc();
}
