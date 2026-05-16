package com.example.petservice.pet;

import com.example.petservice.auth.CurrentUser;
import com.example.petservice.common.NotFoundException;
import com.example.petservice.pet.PetDtos.CreatePetRequest;
import com.example.petservice.pet.PetDtos.PetResponse;
import com.example.petservice.user.User;
import com.example.petservice.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {
  private final PetRepository pets;
  private final UserRepository users;
  private final PetMapper mapper;

  public PetService(PetRepository pets, UserRepository users, PetMapper mapper) {
    this.pets = pets;
    this.users = users;
    this.mapper = mapper;
  }

  @Transactional
  public PetResponse create(CurrentUser currentUser, CreatePetRequest request) {
    User owner = users.findById(currentUser.id()).orElseThrow(() -> new NotFoundException("Owner not found"));
    Pet pet = new Pet();
    pet.setOwner(owner);
    pet.setName(request.name());
    pet.setSpecies(request.species());
    pet.setBreed(request.breed());
    pet.setWeightKg(request.weightKg());
    pet.setNotes(request.notes());
    return mapper.toResponse(pets.save(pet));
  }

  @Transactional(readOnly = true)
  public List<PetResponse> listMine(CurrentUser currentUser) {
    return pets.findByOwnerId(currentUser.id()).stream().map(mapper::toResponse).toList();
  }
}
