package com.example.productinventory.controller;

import com.example.productinventory.dto.DashboardStats;
import com.example.productinventory.model.InventoryTransaction;
import com.example.productinventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final InventoryService inventoryService;

    public DashboardController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        return ResponseEntity.ok(inventoryService.getDashboardStats());
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<InventoryTransaction>> getRecentTransactions() {
        return ResponseEntity.ok(inventoryService.getRecentTransactions());
    }
}
