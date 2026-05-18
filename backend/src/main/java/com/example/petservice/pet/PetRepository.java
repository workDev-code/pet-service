package com.example.petservice.pet;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
  List<Pet> findByOwnerId(Long ownerId);

  boolean existsByOwnerId(Long ownerId);
}
