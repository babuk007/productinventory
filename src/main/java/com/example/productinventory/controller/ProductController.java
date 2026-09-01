package com.example.productinventory.controller;

import com.example.productinventory.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping({"/api/products", "/api/product"})
public class ProductController {

    @GetMapping
    public List<Product> getProducts() {
        return Arrays.asList(
                new Product(1L, "Apple iPhone 15", "Electronics", 79999.00, 25),
                new Product(2L, "Sony WH-1000XM5 Headphones", "Electronics", 29999.00, 40),
                new Product(3L, "Nike Air Max Running Shoes", "Apparel", 8999.00, 15),
                new Product(4L, "Ergonomic Office Chair", "Furniture", 15499.00, 10),
                new Product(5L, "Stainless Steel Water Bottle", "Home & Kitchen", 1499.00, 100),
                new Product(6L, "Dell UltraSharp 27\" 4K Monitor", "Electronics", 34999.00, 12),
                new Product(7L, "Logitech MX Master 3S Mouse", "Electronics", 9499.00, 30),
                new Product(8L, "Levi's 511 Slim Fit Jeans", "Apparel", 3299.00, 50),
                new Product(9L, "Instant Pot Duo 7-in-1 Cooker", "Home & Kitchen", 8499.00, 20),
                new Product(10L, "Minimalist Oak Dining Table", "Furniture", 24999.00, 5)
        );
    }
}