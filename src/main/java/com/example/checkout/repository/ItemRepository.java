package com.example.checkout.repository;

import com.example.checkout.domain.Item;

import java.util.Optional;

public interface ItemRepository {

    Optional<Item> findById(Long id);
}
