package com.example.petservice.pet;

import com.example.petservice.auth.AuthUserDetails;
import com.example.petservice.pet.PetDtos.CreatePetRequest;
import com.example.petservice.pet.PetDtos.PetResponse;
import com.example.petservice.pet.PetDtos.UpdatePetRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

  @PutMapping("/{id}")
  PetResponse update(
      @AuthenticationPrincipal AuthUserDetails user,
      @PathVariable Long id,
      @Valid @RequestBody UpdatePetRequest request
  ) {
    return petService.update(user.currentUser(), id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@AuthenticationPrincipal AuthUserDetails user, @PathVariable Long id) {
    petService.delete(user.currentUser(), id);
  }

  @PostMapping("/{id}/photo")
  PetResponse uploadPhoto(
      @AuthenticationPrincipal AuthUserDetails user,
      @PathVariable Long id,
      @RequestParam("file") MultipartFile file
  ) {
    return petService.uploadPhoto(user.currentUser(), id, file);
  }
}
