package com.example.checkout.payment;

public interface PaymentStrategy {

    boolean pay(String phoneNumber, double amount);
}
