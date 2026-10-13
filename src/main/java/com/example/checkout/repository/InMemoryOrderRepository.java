package com.example.checkout.repository;

import com.example.checkout.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final AtomicLong nextId = new AtomicLong(1);
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(nextId.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }
}
