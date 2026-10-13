package com.example.checkout.repository;

import com.example.checkout.domain.Item;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = Map.of(
            1001L, new Item(1001L, 125000),
            1002L, new Item(1002L, 75000)
    );

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }
}
