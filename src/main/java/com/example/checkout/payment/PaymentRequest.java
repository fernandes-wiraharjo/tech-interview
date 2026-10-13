package com.example.checkout.payment;

public class PaymentRequest {

    private final String phoneNumber;
    private final double amount;
    private final String merchantReference;

    public PaymentRequest(String phoneNumber, double amount, String merchantReference) {
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.merchantReference = merchantReference;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getMerchantReference() {
        return merchantReference;
    }
}
