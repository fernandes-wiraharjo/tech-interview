package com.example.checkout.repository;

import com.example.checkout.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = Map.of(
            "user-123", new User("user-123", "+628123456789")
    );

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }
}
