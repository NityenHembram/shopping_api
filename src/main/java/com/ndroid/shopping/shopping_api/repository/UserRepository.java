package com.ndroid.shopping.shopping_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ndroid.shopping.shopping_api.model.UserModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {
    Optional<UserModel> findByUsername(String username);
}
