package com.ndroid.shopping.shopping_api.dto;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class UserRequestDto {
    private String username;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
    private String password;
}