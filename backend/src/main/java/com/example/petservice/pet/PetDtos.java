package com.example.petservice.pet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public final class PetDtos {
  private PetDtos() {}

  public record CreatePetRequest(
      @NotBlank @Size(max = 80) String name,
      @NotBlank @Size(max = 60) String species,
      @Size(max = 80) String breed,
      @NotNull @DecimalMin("0.10") BigDecimal weightKg,
      @Size(max = 1000) String notes
  ) {}

  public record PetResponse(
      Long id,
      Long ownerId,
      String name,
      String species,
      String breed,
      BigDecimal weightKg,
      String notes
  ) {}
}
