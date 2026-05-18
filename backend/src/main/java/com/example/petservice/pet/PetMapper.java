package com.example.petservice.pet;

import com.example.petservice.pet.PetDtos.PetResponse;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {
  public PetResponse toResponse(Pet pet) {
    return new PetResponse(
        pet.getId(),
        pet.getOwner().getId(),
        pet.getName(),
        pet.getSpecies(),
        pet.getBreed(),
        pet.getWeightKg(),
        pet.getNotes(),
        pet.getPhotoUrl()
    );
  }
}
