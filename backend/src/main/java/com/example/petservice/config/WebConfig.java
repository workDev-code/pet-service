package com.example.petservice.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private final Path petPhotosDir;

  public WebConfig(@Value("${app.uploads.pet-photos-dir}") String petPhotosDir) {
    this.petPhotosDir = Path.of(petPhotosDir).toAbsolutePath().normalize();
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/uploads/pets/**")
        .addResourceLocations(petPhotosDir.toUri().toString() + "/");
  }
}
