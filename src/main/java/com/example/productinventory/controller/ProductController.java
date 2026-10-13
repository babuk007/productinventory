package com.example.productinventory.controller;

import com.example.productinventory.dto.ProductRequest;
import com.example.productinventory.dto.StockAdjustmentRequest;
import com.example.productinventory.model.Product;
import com.example.productinventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping({"/api/products", "/api/product"})
@CrossOrigin(origins = "*")
public class ProductController {

    private final InventoryService inventoryService;

    public ProductController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        List<Product> products = inventoryService.getAllProducts(search, categoryId, status);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = inventoryService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        Product created = inventoryService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Product updated = inventoryService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/adjust-stock")
    public ResponseEntity<Product> adjustStock(@PathVariable Long id, @Valid @RequestBody StockAdjustmentRequest request) {
        Product updated = inventoryService.adjustStock(id, request);
        return ResponseEntity.ok(updated);
    }
}
