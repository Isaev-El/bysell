package com.example.bysell.controllers;


import com.example.bysell.models.Product;
import com.example.bysell.models.User;
import com.example.bysell.services.ProductService;
import com.example.bysell.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @GetMapping("/user/{user}")
    public String userInfo(@PathVariable User user, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        model.addAttribute("user",user);
        model.addAttribute("currentUserEmail",currentUserEmail);
        model.addAttribute("products", user.getProducts());
        return "user-info";
    }

    @PostMapping("/registration")
    public String createUser(User user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errormessage", "User with email:" + user.getEmail() + " already exist!");
            return "registration";
        }
        userService.createUser(user);
        return "redirect:/login";
    }

    @PostMapping("/user/uploadavatar")
    public String uploadAvatar(@RequestParam("userId") Long userId, MultipartFile file) throws IOException {
        userService.uploadAvatar(file);
        // Ваша логика обновления аватарки пользователя по userId
        return "redirect:/user/" + userId;
    }

}
