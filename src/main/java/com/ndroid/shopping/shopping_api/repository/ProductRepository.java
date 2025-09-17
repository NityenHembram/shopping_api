package com.ndroid.shopping.shopping_api.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ndroid.shopping.shopping_api.model.Products;


public interface ProductRepository extends JpaRepository<Products, Integer> {
    @Query(value = "SELECT * FROM products", nativeQuery = true)
List<Products> rawQuery();
}
