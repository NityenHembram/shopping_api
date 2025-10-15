package com.ndroid.shopping.shopping_api.controller;

import com.ndroid.shopping.shopping_api.dto.LoginRequestDto;
import com.ndroid.shopping.shopping_api.service.AuthService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ndroid.shopping.shopping_api.dto.UserRequestDto;
import com.ndroid.shopping.shopping_api.model.CommonResponse;



@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthContoller {

    private AuthService authService;


    @PostMapping(value = "/register")
    public ResponseEntity<CommonResponse> registerUserController(@RequestBody UserRequestDto UserDto){
        return authService.registerUser(UserDto);
    }

     @PostMapping(value = "/login")
     public ResponseEntity<Object> loginController(@RequestBody LoginRequestDto loginRequestDto) {
         return authService.login(loginRequestDto);
     }
     
     @PostMapping(value = "/refresh-token")
     public ResponseEntity<Object> refreshToken(@RequestParam String refreshToken) {
         return authService.refreshToken(refreshToken);
     }


}
