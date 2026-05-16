package com.example.petservice.pet;

import com.example.petservice.auth.AuthUserDetails;
import com.example.petservice.pet.PetDtos.CreatePetRequest;
import com.example.petservice.pet.PetDtos.PetResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
public class PetController {
  private final PetService petService;

  public PetController(PetService petService) {
    this.petService = petService;
  }

  @PostMapping
  PetResponse create(@AuthenticationPrincipal AuthUserDetails user, @Valid @RequestBody CreatePetRequest request) {
    return petService.create(user.currentUser(), request);
  }

  @GetMapping
  List<PetResponse> listMine(@AuthenticationPrincipal AuthUserDetails user) {
    return petService.listMine(user.currentUser());
  }
}
