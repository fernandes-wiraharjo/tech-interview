package com.example.checkout.repository;

import com.example.checkout.domain.Order;

public interface OrderRepository {

    Order save(Order order);
}
