package com.example.checkout.repository;

import com.example.checkout.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(String id);
}
