package com.example.petservice.pet;

import com.example.petservice.auth.CurrentUser;
import com.example.petservice.booking.BookingRepository;
import com.example.petservice.common.BadRequestException;
import com.example.petservice.common.ForbiddenException;
import com.example.petservice.common.NotFoundException;
import com.example.petservice.pet.PetDtos.CreatePetRequest;
import com.example.petservice.pet.PetDtos.PetResponse;
import com.example.petservice.pet.PetDtos.UpdatePetRequest;
import com.example.petservice.user.Role;
import com.example.petservice.user.User;
import com.example.petservice.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {
  private final PetRepository pets;
  private final UserRepository users;
  private final BookingRepository bookings;
  private final PetMapper mapper;
  private final PetPhotoStorageService photoStorage;

  public PetService(
      PetRepository pets,
      UserRepository users,
      BookingRepository bookings,
      PetMapper mapper,
      PetPhotoStorageService photoStorage
  ) {
    this.pets = pets;
    this.users = users;
    this.bookings = bookings;
    this.mapper = mapper;
    this.photoStorage = photoStorage;
  }

  @Transactional
  public PetResponse create(CurrentUser currentUser, CreatePetRequest request) {
    requireCustomer(currentUser);
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

  @Transactional
  public PetResponse update(CurrentUser currentUser, Long id, UpdatePetRequest request) {
    requireCustomer(currentUser);
    Pet pet = findOwnedPet(currentUser, id);
    pet.setName(request.name());
    pet.setSpecies(request.species());
    pet.setBreed(request.breed());
    pet.setWeightKg(request.weightKg());
    pet.setNotes(request.notes());
    return mapper.toResponse(pet);
  }

  @Transactional
  public void delete(CurrentUser currentUser, Long id) {
    requireCustomer(currentUser);
    Pet pet = findOwnedPet(currentUser, id);
    if (bookings.existsByPetId(id)) {
      throw new BadRequestException("Cannot delete a pet that already has bookings");
    }
    photoStorage.delete(pet.getPhotoUrl());
    pets.delete(pet);
  }

  @Transactional
  public PetResponse uploadPhoto(CurrentUser currentUser, Long id, org.springframework.web.multipart.MultipartFile file) {
    requireCustomer(currentUser);
    Pet pet = findOwnedPet(currentUser, id);
    pet.setPhotoUrl(photoStorage.replace(id, file, pet.getPhotoUrl()));
    return mapper.toResponse(pet);
  }

  private Pet findOwnedPet(CurrentUser currentUser, Long id) {
    Pet pet = pets.findById(id).orElseThrow(() -> new NotFoundException("Pet not found"));
    if (!pet.getOwner().getId().equals(currentUser.id())) {
      throw new ForbiddenException("Cannot manage another customer's pet");
    }
    return pet;
  }

  private void requireCustomer(CurrentUser currentUser) {
    if (currentUser.role() != Role.CUSTOMER) {
      throw new ForbiddenException("Only customers can manage pets");
    }
  }
}
