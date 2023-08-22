package com.example.bysell.services;


import com.example.bysell.models.Image;
import com.example.bysell.models.Product;
import com.example.bysell.models.User;
import com.example.bysell.repositories.ImageRepository;
import com.example.bysell.repositories.ProductRepository;
import com.example.bysell.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<Product> listOfProducts(String title) {
        List<Product> matchingProducts = new ArrayList<>();
        if (title != null) {
            List<String> allProducts = findSame(title);
            for (String allP : allProducts) {
                List<Product> temp = productRepository.findByTitle(allP);
                matchingProducts.addAll(temp);
            }
            return matchingProducts;
        }
        return productRepository.findAll();
    }

    public List<String> findSame(String title) {
        List<String> database = new ArrayList<>();
        List<Product> products = productRepository.findAll();

        int minDistance = Integer.MAX_VALUE;
        String closestMatch = "";

        for (Product product : products) {
            database.add(product.getTitle());
        }
        List<String> matchingResults = new ArrayList<>();
        for (String item : database) {
            int distance = StringUtils.getLevenshteinDistance(item.toLowerCase(), title.toLowerCase());
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch=item;
            }
        }
        matchingResults.add(closestMatch);
        return matchingResults;
    }

    public void saveProduct(Principal principal, Product product, List<MultipartFile> files) throws IOException {
        product.setUser(getUserByPrincipal(principal));
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
                File kila = new File(filePath);
                image.setPath(filePath);
                if (isFirstImage) {
                    image.setPreviewImage(true);
                    isFirstImage = false;
                }
                file.transferTo(kila);
                product.addImageToProject(image);
            }
        }
        log.debug("Saving new Product. Title: {}", product.getTitle());
        Product productFromDb = productRepository.save(product);
        productFromDb.setPreviewImageId(productFromDb.getImages().get(0).getId());
        productRepository.saveAndFlush(product);
    }

    public User getUserByPrincipal(Principal principal){
        if (principal==null) return new User();
        return userRepository.findByEmail(principal.getName());
    }

    public Image toImageEntity(MultipartFile file) throws IOException {
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
            Image image = product.getImages().get(0);
            String path = image.getPath();
            String[] parts = path.split("\\\\");
            List<String> finalParts = new ArrayList<>();
            for (int i=0;i<parts.length-1;i++){
                finalParts.add(parts[i]);
            }
            String result = String.join("\\\\",finalParts);
            deleteFolder(result);
         // Удалить продукт из базы данных
            productRepository.deleteById(id);
        }
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }


}
