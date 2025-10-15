package com.ndroid.shopping.shopping_api.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndroid.shopping.shopping_api.dto.ApiResponseDto;
import com.ndroid.shopping.shopping_api.exception.BusinessException;
import com.ndroid.shopping.shopping_api.exception.ErrorCodes;
import com.ndroid.shopping.shopping_api.model.Products;
import com.ndroid.shopping.shopping_api.repository.ProductRepository;
import com.ndroid.shopping.shopping_api.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private ProductService productService;

    @Autowired
    private ProductRepository repo;

    public ProductController(ProductService service) {
        this.productService = service;
    }

    @PostMapping(value = "/upload-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<String>> uploadProductWithImage(
            @RequestPart("product") String productJson,
            @RequestPart("image") MultipartFile imageFile) {
        try {
            if (imageFile.isEmpty()) {
                throw new BusinessException("Image file is required", ErrorCodes.VALIDATION_ERROR);
            }

            // Deserialize JSON string manually
            ObjectMapper mapper = new ObjectMapper();
            Products product = mapper.readValue(productJson, Products.class);

            product.setImage(imageFile.getBytes());
            productService.saveProduct(product);

            ApiResponseDto<String> response = ApiResponseDto.success(
                    "Product uploaded successfully",
                    "Product uploaded with image.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            throw new BusinessException("Failed to process image file", ErrorCodes.BUSINESS_ERROR, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<Products>>> getAllProducts() {
        List<Products> products = repo.rawQuery();
        ApiResponseDto<List<Products>> response = ApiResponseDto.success(
                "Products retrieved successfully",
                products);
        return ResponseEntity.ok(response);
    }

}
