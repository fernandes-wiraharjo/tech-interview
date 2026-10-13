package com.example.checkout.payment;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("gopay")
public class GopayPaymentStrategy implements PaymentStrategy {

    // Exercise fake: do not call the real provider from local tests or interview code.
    private final ConcurrentMap<String, PaymentResult> fakeProviderTransactions = new ConcurrentHashMap<>();

    @Override
    public PaymentResult pay(PaymentRequest request) {
        PaymentResult result = PaymentResult.captured("gopay-" + request.getMerchantReference());
        fakeProviderTransactions.put(request.getMerchantReference(), result);
        return result;
    }

    @Override
    public PaymentResult findByMerchantReference(String merchantReference) {
        return fakeProviderTransactions.getOrDefault(
                merchantReference,
                PaymentResult.unknown("Provider has no final status for this merchant reference yet")
        );
    }
}
