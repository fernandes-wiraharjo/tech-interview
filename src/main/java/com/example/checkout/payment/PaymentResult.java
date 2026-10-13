package com.example.checkout.payment;

// Models provider uncertainty explicitly; UNKNOWN must not be treated as failure.
public class PaymentResult {

    private final PaymentStatus status;
    private final String providerReference;
    private final String message;

    private PaymentResult(PaymentStatus status, String providerReference, String message) {
        this.status = status;
        this.providerReference = providerReference;
        this.message = message;
    }

    public static PaymentResult captured(String providerReference) {
        return new PaymentResult(PaymentStatus.CAPTURED, providerReference, "Payment captured");
    }

    public static PaymentResult failed(String message) {
        return new PaymentResult(PaymentStatus.FAILED, null, message);
    }

    public static PaymentResult unknown(String message) {
        return new PaymentResult(PaymentStatus.UNKNOWN, null, message);
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public String getMessage() {
        return message;
    }
}
