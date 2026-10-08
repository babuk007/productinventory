package com.example.productinventory.dto;

public class DashboardStats {

    private long totalProducts;
    private long totalQuantity;
    private double totalInventoryValue;
    private long lowStockCount;
    private long outOfStockCount;
    private long categoryCount;

    public DashboardStats() {
    }

    public DashboardStats(long totalProducts, long totalQuantity, double totalInventoryValue, long lowStockCount, long outOfStockCount, long categoryCount) {
        this.totalProducts = totalProducts;
        this.totalQuantity = totalQuantity;
        this.totalInventoryValue = totalInventoryValue;
        this.lowStockCount = lowStockCount;
        this.outOfStockCount = outOfStockCount;
        this.categoryCount = categoryCount;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(double totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public long getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(long outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }

    public long getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(long categoryCount) {
        this.categoryCount = categoryCount;
    }
}
