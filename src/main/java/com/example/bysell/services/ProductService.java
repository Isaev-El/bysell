package com.example.bysell.services;


import com.example.bysell.models.Image;
import com.example.bysell.models.Product;
import com.example.bysell.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> listOfProducts(String title) {
        if (title != null) return productRepository.findByTitle(title);
        return productRepository.findAll();
    }

    public void saveProduct(Product product, List<MultipartFile> files) throws IOException {
        boolean isFirstImage = true;
        String uploadDir = "C:\\Users\\Admin\\Desktop\\bysell\\images";
        String ID = String.valueOf(UUID.randomUUID());
        String productDir = uploadDir + File.separator + ID;
        File directory = new File(productDir);
        try {
            if (!Files.exists(directory.toPath())) {
                Files.createDirectories(directory.toPath());
                System.out.println("Папка успешно создана");
            } else {
                System.out.println("Папка уже существует");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании папки: " + e.getMessage());
        } // Создать директорию, если она не существует
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Image image = toImageEntity(file);
                String fileName = file.getOriginalFilename();
                String filePath = productDir + File.separator + fileName;
                File qotaq = new File(filePath);
                image.setPath(filePath);
                if (isFirstImage) {
                    image.setPreviewImage(true);
                    isFirstImage = false;
                }
                file.transferTo(qotaq);
                product.addImageToProject(image);
            }
        }
        log.debug("Saving new Product. Title: {}; Author: {}", product.getTitle(), product.getAuthor());
        Product productFromDb = productRepository.save(product);
        productFromDb.setPreviewImageId(productFromDb.getImages().get(0).getId());
        productRepository.saveAndFlush(product);
    }

    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public void deleteFolder(String folderPath) {
        try {
            Path directory = Paths.get(folderPath);
            if (Files.exists(directory)) {
                Files.walk(directory)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                System.out.println("Папка успешно удалена: " + folderPath);
            } else {
                System.out.println("Папка не найдена: " + folderPath);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при удалении папки: " + e.getMessage());
        }
    }

    public void deleteProduct(Long id) {
        // Получить информацию о продукте
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            // Удалить фотографии из директории
            Image image = product.getImages().get(0);
            String filePath = image.getPath();
            String[] parts = filePath.split("\\\\");
            List<String> finalPath=new ArrayList<>();
            for (int i=0;i<parts.length-1;i++){
                finalPath.add(parts[i]);
            }
            String result=String.join("\\",finalPath);
            deleteFolder(result);
            // Удалить продукт из базы данных
            productRepository.deleteById(id);
        }
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }


}
