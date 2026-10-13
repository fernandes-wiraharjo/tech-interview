package com.example.checkout.api;

public class PurchaseResponse {

    private boolean success;
    private double totalAmount;
    private String message;

    public PurchaseResponse() {
    }

    public PurchaseResponse(boolean success, double totalAmount, String message) {
        this.success = success;
        this.totalAmount = totalAmount;
        this.message = message;
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
}
