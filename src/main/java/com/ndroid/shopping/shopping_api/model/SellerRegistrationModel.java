package com.ndroid.shopping.shopping_api.model;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class SellerRegistrationModel {

    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;

    
}
