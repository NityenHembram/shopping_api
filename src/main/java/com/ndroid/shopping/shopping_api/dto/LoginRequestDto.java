package com.ndroid.shopping.shopping_api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoginRequestDto {

        @JsonProperty("user_name")
        public String username;

        @JsonProperty("password")
        public String password;
        
}
