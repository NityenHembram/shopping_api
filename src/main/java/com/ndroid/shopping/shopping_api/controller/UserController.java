package com.ndroid.shopping.shopping_api.controller;

import com.ndroid.shopping.shopping_api.dto.CommonResponse;
import com.ndroid.shopping.shopping_api.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    @GetMapping("/listUser")
    public ResponseEntity<Object> getAllUsers() {
        return service.getAllUserData();
    }


    @GetMapping("/getUserById")
    public ResponseEntity<CommonResponse> getUserById(Long id) {
        return service.getUserById(id);
    }


}
