package com.example.productinventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {

    @NotBlank(message = "Adjustment type is required (RESTOCK, DISPATCH, ADJUSTMENT)")
    private String type;

    @NotNull(message = "Quantity change is required")
    private Integer quantity;

    private String reason;

    public StockAdjustmentRequest() {
    }

    public StockAdjustmentRequest(String type, Integer quantity, String reason) {
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
