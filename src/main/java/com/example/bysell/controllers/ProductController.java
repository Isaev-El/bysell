package com.example.bysell.controllers;

import com.example.bysell.models.Image;
import com.example.bysell.models.Product;
import com.example.bysell.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor

public class ProductController {

        private final ProductService productService;

        @GetMapping("/")
        public String products(@RequestParam(name = "title", required = false) String title, Model model) {
            model.addAttribute("products", productService.listOfProducts(title));
            return "products";
        }


        @GetMapping("/product/{id}")
        public String productInfo(@PathVariable Long id, Model model) {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("images", product.getImages());
            return "product-info";
        }
        @PostMapping("/product/create")
        public String createProduct(@RequestParam("file") List<MultipartFile> files, Product product) throws IOException {
            if (!files.isEmpty()) {
                productService.saveProduct(product, files);
            }
            return "redirect:/";
        }

        @PostMapping("/product/delete/{id}")
        public String deleteProduct(@PathVariable Long id) {
            productService.deleteProduct(id);
            return "redirect:/";
        }
    }