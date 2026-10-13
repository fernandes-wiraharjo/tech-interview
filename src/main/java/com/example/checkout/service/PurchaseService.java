package com.example.checkout.service;

import com.example.checkout.api.PurchaseRequest;
import com.example.checkout.api.PurchaseResponse;
import com.example.checkout.domain.Item;
import com.example.checkout.domain.Order;
import com.example.checkout.domain.OrderStatus;
import com.example.checkout.domain.User;
import com.example.checkout.payment.PaymentRequest;
import com.example.checkout.payment.PaymentResult;
import com.example.checkout.payment.PaymentStrategy;
import com.example.checkout.payment.PaymentStatus;
import com.example.checkout.repository.ItemRepository;
import com.example.checkout.repository.OrderRepository;
import com.example.checkout.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private Map<String, PaymentStrategy> paymentStrategies;

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    @Transactional
    public PurchaseResponse purchase(PurchaseRequest request) {
        validateRequest(request);

        String merchantReference = "checkout-" + request.getIdempotencyKey();
        String requestFingerprint = fingerprint(request);

        Order existingOrder = orderRepository.findByMerchantReference(merchantReference).orElse(null);
        if (existingOrder != null) {
            // Same key with different purchase data means the client is reusing the key incorrectly.
            if (!requestFingerprint.equals(existingOrder.getRequestFingerprint())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "idempotencyKey was already used for a different purchase");
            }
            // Retry path: reconcile by merchant reference instead of sending another charge.
            return reconcileAndRespond(existingOrder, paymentStrategy(existingOrder.getPaymentMethod()));
        }

        PaymentStrategy strategy = paymentStrategy(request.getPaymentMethod());
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown user"));

        double total = calculateTrustedTotal(request);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalAmount(total);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setMerchantReference(merchantReference);
        order.setRequestFingerprint(requestFingerprint);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        log.info("Charging user {} phone {} amount {}",
                user.getId(), user.getPhoneNumber(), total);

        PaymentResult paymentResult = strategy.pay(new PaymentRequest(user.getPhoneNumber(), total, merchantReference));
        applyPaymentResult(order, paymentResult);
        orderRepository.save(order);

        return toResponse(order);
    }

    private PaymentStrategy paymentStrategy(String paymentMethod) {
        PaymentStrategy strategy = paymentStrategies.get(paymentMethod);
        if (strategy == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported payment method");
        }
        return strategy;
    }

    private void validateRequest(PurchaseRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (isBlank(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        if (isBlank(request.getPaymentMethod())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paymentMethod is required");
        }
        if (isBlank(request.getIdempotencyKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idempotencyKey is required for safe retries");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one item is required");
        }
        for (PurchaseRequest.ItemLine line : request.getItems()) {
            if (line.getItemId() == null || line.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item id and positive quantity are required");
            }
        }
    }

    private double calculateTrustedTotal(PurchaseRequest request) {
        double total = 0;
        for (PurchaseRequest.ItemLine line : request.getItems()) {
            // Client-supplied unitPrice is ignored; pricing authority is the server-side item store.
            Item item = itemRepository.findById(line.getItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown item"));
            total += item.getUnitPrice() * line.getQuantity();
        }
        return total;
    }

    private PurchaseResponse reconcileAndRespond(Order order, PaymentStrategy strategy) {
        if (OrderStatus.PENDING.equals(order.getStatus())) {
            PaymentResult paymentResult = strategy.findByMerchantReference(order.getMerchantReference());
            applyPaymentResult(order, paymentResult);
            orderRepository.save(order);
        }
        return toResponse(order);
    }

    private void applyPaymentResult(Order order, PaymentResult paymentResult) {
        if (PaymentStatus.CAPTURED.equals(paymentResult.getStatus())) {
            order.setStatus(OrderStatus.PAID);
            order.setProviderReference(paymentResult.getProviderReference());
        } else if (PaymentStatus.FAILED.equals(paymentResult.getStatus())) {
            order.setStatus(OrderStatus.FAILED);
        } else {
            // Unknown means the local service lost certainty, not that the customer was not charged.
            order.setStatus(OrderStatus.PENDING);
        }
    }

    private PurchaseResponse toResponse(Order order) {
        boolean paid = OrderStatus.PAID.equals(order.getStatus());
        String message = paid ? "OK" : "Payment " + order.getStatus().name().toLowerCase();
        return new PurchaseResponse(paid, order.getTotalAmount(), message, order.getId(), order.getStatus().name());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String fingerprint(PurchaseRequest request) {
        String items = request.getItems().stream()
                .sorted(Comparator.comparing(PurchaseRequest.ItemLine::getItemId))
                .map(line -> line.getItemId() + "x" + line.getQuantity())
                .collect(Collectors.joining("|"));
        return request.getUserId() + ":" + request.getPaymentMethod() + ":" + items;
    }
}
