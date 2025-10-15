package com.ndroid.shopping.shopping_api.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
public class UserModelDto {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
}
