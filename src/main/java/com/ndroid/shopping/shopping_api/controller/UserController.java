package com.ndroid.shopping.shopping_api.controller;


import com.ndroid.shopping.shopping_api.model.UserModel;
import com.ndroid.shopping.shopping_api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserRepository repository;

    @GetMapping("/listUser")
    public ResponseEntity<List<UserModel>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(repository.findAll());
    }

}
