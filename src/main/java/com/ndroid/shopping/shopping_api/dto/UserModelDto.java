package com.ndroid.shopping.shopping_api.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserModelDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
}
