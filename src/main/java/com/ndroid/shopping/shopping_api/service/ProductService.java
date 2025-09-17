package com.ndroid.shopping.shopping_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ndroid.shopping.shopping_api.model.Products;
import com.ndroid.shopping.shopping_api.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    public ProductService(ProductRepository repo){
        this.productRepository = repo;
    }

    public Products saveProduct(Products product){
        return productRepository.save(product);
    }
    

    public List<Products> getAllProduct(){
        return productRepository.findAll();
    }
}
