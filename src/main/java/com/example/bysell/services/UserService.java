package com.example.bysell.services;


import com.example.bysell.models.Image;
import com.example.bysell.models.User;
import com.example.bysell.models.enums.Role;
import com.example.bysell.repositories.ImageRepository;
import com.example.bysell.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;

    public boolean createUser(User user) {
        String email = user.getEmail();
        String presentPath = "C:\\Users\\Admin\\Desktop\\bysell\\images\\avatar\\client-img.png";
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return false;
        }
        Image avatarImage = new Image();
        avatarImage.setName("file"); // Название аватарки
        avatarImage.setOriginalFileName("client-img.png"); // Имя файла
        avatarImage.setContentType("image/jpeg"); // Тип контента
        avatarImage.setPath(presentPath); // Путь к файлу на сервере
        avatarImage.setUser(user); // Свяжите аватарку с пользователем
        avatarImage.setAvatarPath(presentPath);
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        user.setAvatar(avatarImage);
        log.info("Saving new User with email:{}", email);
        userRepository.save(user);
        return true;
    }

    public void uploadAvatar(MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = productService.getUserByPrincipal(authentication);
        String avatarPath = "C:\\Users\\Admin\\Desktop\\bysell\\images";
        Image image = productService.toImageEntity(file);
        Image imageAvatar = imageUserToNewImage(authentication, image);
        imageAvatar.setAvatarPath(avatarPath);
        String productDir = avatarPath + File.separator + user.getId();
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
        String fileName = file.getOriginalFilename();
        String filePath = productDir + File.separator + fileName;
        File gorilla = new File(filePath);
        imageAvatar.setPath(filePath);
        file.transferTo(gorilla);
        imageAvatar.setUser(user);
        imageRepository.save(imageAvatar);
    }

    public Image imageUserToNewImage(Principal principal, Image image) {
        User user = productService.getUserByPrincipal(principal);
        Image imageAvatar = user.getAvatar();
        imageAvatar.setName(image.getName());
        imageAvatar.setOriginalFileName(image.getOriginalFileName());
        imageAvatar.setContentType(image.getContentType());
        imageAvatar.setSize(image.getSize());
        imageAvatar.setBytes(image.getBytes());
        return imageAvatar;
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    public void banUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if (user.isActive()) {
                user.setActive(false);
                log.info("user {} is banned", user.getEmail());
            } else {
                user.setActive(true);
                log.info("user {} is unbanned", user.getEmail());
            }
        } else {
            log.info("user is null");
        }
        assert user != null;
        userRepository.save(user);
    }

    public void changeUserRoles(User user, Map<String, String> form) {
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepository.save(user);
    }
}
