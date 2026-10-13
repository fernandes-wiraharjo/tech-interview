package com.example.checkout.api;

public class PurchaseResponse {

    private boolean success;
    private double totalAmount;
    private String message;
    // Exposes the persisted order and state so clients can treat PENDING differently from FAILED.
    private Long orderId;
    private String status;

    public PurchaseResponse() {
    }

    public PurchaseResponse(boolean success, double totalAmount, String message) {
        this.success = success;
        this.totalAmount = totalAmount;
        this.message = message;
    }

    public PurchaseResponse(boolean success, double totalAmount, String message, Long orderId, String status) {
        this.success = success;
        this.totalAmount = totalAmount;
        this.message = message;
        this.orderId = orderId;
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
