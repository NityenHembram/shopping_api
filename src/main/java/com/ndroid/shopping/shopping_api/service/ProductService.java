package com.ndroid.shopping.shopping_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ndroid.shopping.shopping_api.dto.CommonResponse;
import com.ndroid.shopping.shopping_api.model.Product;
import com.ndroid.shopping.shopping_api.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    public ProductService(ProductRepository repo) {
        this.productRepository = repo;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public ResponseEntity<CommonResponse> getAllProduct() {
       List<Product> products = productRepository.rawQuery();
        CommonResponse response = new
        CommonResponse(200,
            "Success",
            products
        );
        return ResponseEntity.ok(response);
    }
}
