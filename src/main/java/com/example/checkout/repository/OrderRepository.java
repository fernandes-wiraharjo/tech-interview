package com.example.checkout.repository;

import com.example.checkout.domain.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByMerchantReference(String merchantReference);
}
