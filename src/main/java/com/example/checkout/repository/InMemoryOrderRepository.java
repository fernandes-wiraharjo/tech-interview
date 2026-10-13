package com.example.checkout.repository;

import com.example.checkout.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final AtomicLong nextId = new AtomicLong(1);
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    // In a real database this should be a unique index to enforce idempotency under concurrency.
    private final Map<String, Long> ordersByMerchantReference = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(nextId.getAndIncrement());
        }
        orders.put(order.getId(), order);
        if (order.getMerchantReference() != null) {
            ordersByMerchantReference.put(order.getMerchantReference(), order.getId());
        }
        return order;
    }

    @Override
    public Optional<Order> findByMerchantReference(String merchantReference) {
        Long orderId = ordersByMerchantReference.get(merchantReference);
        if (orderId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(orders.get(orderId));
    }
}
