package com.example.productinventory.controller;

import com.example.productinventory.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public Product getProduct() {

        return new Product(
                1L,
                "Apple iPhone 15",
                "Electronics",
                79999.00,
                25
        );
    }
}