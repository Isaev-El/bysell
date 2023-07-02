package com.example.bysell.controllers;
import com.example.bysell.models.Image;
import com.example.bysell.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
public class ImageController {

    private final ImageRepository imageRepository;

    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }


    public Resource copyImageToResource(String imagePath) throws IOException {
        // Создание временного файла для сохранения скопированной фотографии
        String[] S=imagePath.split("\\.");
        File tempFile = File.createTempFile("temp", "."+S[S.length-1]);
        // Копирование фотографии по указанному пути во временный файл
        Path sourcePath = Path.of(imagePath);
        Path targetPath = tempFile.toPath();
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Преобразование временного файла в объект класса Resource
        Resource resource = new FileSystemResource(tempFile);

        return resource;
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<Resource> getImageById(@PathVariable Long id) throws IOException {
        Image image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        String imagePath = image.getPath();
        if (imagePath == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        Resource fileResource = copyImageToResource(imagePath);
        System.out.println(imagePath);
        if (!fileResource.exists()) {
            return ResponseEntity.notFound().build();
        }
        System.out.println(imagePath);

        return ResponseEntity.ok()
                .header("fileName", image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getSize())
                .body(fileResource);
    }
}