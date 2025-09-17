package com.ndroid.shopping.shopping_api.dto;

import com.ndroid.shopping.shopping_api.model.CommonResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    public String authToken;
    public String refreshToken;
    public CommonResponse response;
}
