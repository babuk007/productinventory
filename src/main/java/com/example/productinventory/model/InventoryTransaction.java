package com.example.productinventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 30)
    private String transactionType; // RESTOCK, DISPATCH, ADJUSTMENT

    @Column(nullable = false)
    private Integer quantityChange;

    @Column(nullable = false)
    private Integer remainingQuantity;

    @Column(length = 255)
    private String reason;

    private LocalDateTime timestamp;

    public InventoryTransaction() {
    }

    public InventoryTransaction(Product product, String transactionType, Integer quantityChange, Integer remainingQuantity, String reason) {
        this.product = product;
        this.transactionType = transactionType;
        this.quantityChange = quantityChange;
        this.remainingQuantity = remainingQuantity;
        this.reason = reason;
    }

    @PrePersist
    public void onPrePersist() {
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
