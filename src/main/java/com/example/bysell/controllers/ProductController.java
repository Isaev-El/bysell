package com.example.bysell.controllers;

import com.example.bysell.models.Image;
import com.example.bysell.models.Product;
import com.example.bysell.models.User;
import com.example.bysell.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequiredArgsConstructor

public class ProductController {

        private final ProductService productService;

        @GetMapping("/")
        public String products(@RequestParam(name = "title", required = false) String title,Principal principal ,Model model) {
            model.addAttribute("products", productService.listOfProducts(title));
            model.addAttribute("user", productService.getUserByPrincipal(principal));
            return "products";
        }


        @GetMapping("/product/{id}")
        public String productInfo(@PathVariable Long id, Model model) {
            Product product = productService.getProductById(id);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            List<String> roleName = new ArrayList<>();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                 roleName.add(authority.getAuthority());
            }
            model.addAttribute("currentUserEmail", currentUserEmail);
            model.addAttribute("currentUserHasRole", roleName);
            model.addAttribute("product", product);
            model.addAttribute("images", product.getImages());
            return "product-info";
        }

        @PostMapping("/product/create")
        public String createProduct(@RequestParam("file") List<MultipartFile> files, Product product, Principal principal) throws IOException {
            if (!files.isEmpty()) {
                productService.saveProduct(principal, product, files);
            }
            return "redirect:/";
        }

        @PostMapping("/product/delete/{id}")
        public String deleteProduct(@PathVariable Long id) {
            productService.deleteProduct(id);
            return "redirect:/";
        }
    }