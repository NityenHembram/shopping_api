package com.ndroid.shopping.shopping_api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginRequestDto {

        @JsonProperty("email")
        public String email;

        @JsonProperty("password")
        public String password;
        
}
