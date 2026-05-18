package com.example.petservice.pet;

import com.example.petservice.common.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PetPhotoStorageService {
  private static final Logger log = LoggerFactory.getLogger(PetPhotoStorageService.class);
  private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE = Map.of(
      "image/jpeg", ".jpg",
      "image/png", ".png",
      "image/webp", ".webp"
  );

  private final Path petPhotosDir;

  public PetPhotoStorageService(@Value("${app.uploads.pet-photos-dir}") String petPhotosDir) {
    this.petPhotosDir = Path.of(petPhotosDir).toAbsolutePath().normalize();
  }

  public String store(Long petId, MultipartFile file) {
    if (file.isEmpty()) {
      throw new BadRequestException("Photo file is required");
    }

    String extension = EXTENSIONS_BY_CONTENT_TYPE.get(file.getContentType());
    if (extension == null) {
      throw new BadRequestException("Only JPG, PNG, and WebP pet photos are supported");
    }

    try {
      Files.createDirectories(petPhotosDir);
      String filename = petId + "-" + UUID.randomUUID() + extension;
      Path destination = petPhotosDir.resolve(filename).normalize();
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
      }
      return "/uploads/pets/" + filename;
    } catch (IOException ex) {
      throw new BadRequestException("Could not store pet photo");
    }
  }

  public String replace(Long petId, MultipartFile file, String oldPhotoUrl) {
    String newPhotoUrl = store(petId, file);
    delete(oldPhotoUrl);
    return newPhotoUrl;
  }

  public void delete(String photoUrl) {
    if (photoUrl == null || !photoUrl.startsWith("/uploads/pets/")) {
      return;
    }

    String filename = Path.of(photoUrl).getFileName().toString();
    Path file = petPhotosDir.resolve(filename).normalize();
    if (!file.startsWith(petPhotosDir)) {
      return;
    }

    try {
      Files.deleteIfExists(file);
    } catch (IOException ex) {
      log.warn("Could not delete pet photo file {}", file, ex);
    }
  }
}
