package com.example.productinventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer minThreshold;

    @Column(length = 50)
    private String location;

    @Column(nullable = false, length = 30)
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Product(String sku, String name, String description, Category category, Double price, Integer quantity, Integer minThreshold, String location) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minThreshold = minThreshold != null ? minThreshold : 10;
        this.location = location;
        updateStatus();
    }

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    public void updateStatus() {
        if (this.quantity == null || this.quantity <= 0) {
            this.status = "OUT_OF_STOCK";
        } else if (this.minThreshold != null && this.quantity <= this.minThreshold) {
            this.status = "LOW_STOCK";
        } else {
            this.status = "IN_STOCK";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        updateStatus();
    }

    public Integer getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(Integer minThreshold) {
        this.minThreshold = minThreshold;
        updateStatus();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
