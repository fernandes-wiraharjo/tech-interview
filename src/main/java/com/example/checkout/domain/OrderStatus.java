package com.example.checkout.domain;

// PENDING is intentional: a provider timeout is not proof that payment failed.
public enum OrderStatus {
    PENDING,
    PAID,
    FAILED
}
