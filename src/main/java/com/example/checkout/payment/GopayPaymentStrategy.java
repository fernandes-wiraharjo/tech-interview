package com.example.checkout.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component("gopay")
public class GopayPaymentStrategy implements PaymentStrategy {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean pay(String phoneNumber, double amount) {
        String url = "https://www.gopay.com/user/" + phoneNumber + "/pay";
        try {
            Map<String, Object> body = Map.of("amount", amount);
            ResponseEntity<String> response = restTemplate.postForEntity(url, body, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception exception) {
            return false;
        }
    }
}
