package com.example.checkout.payment;

public interface PaymentStrategy {

    PaymentResult pay(PaymentRequest request);

    // Used after timeouts/retries to resolve the real provider state without charging again.
    PaymentResult findByMerchantReference(String merchantReference);
}
