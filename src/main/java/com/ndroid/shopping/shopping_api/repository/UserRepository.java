package com.ndroid.shopping.shopping_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndroid.shopping.shopping_api.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
