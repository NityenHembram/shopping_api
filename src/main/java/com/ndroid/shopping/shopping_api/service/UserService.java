package com.ndroid.shopping.shopping_api.service;

import java.time.LocalDateTime;

import javax.imageio.spi.RegisterableService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ndroid.shopping.shopping_api.dto.UserRequestDto;
import com.ndroid.shopping.shopping_api.model.CommonResponse;
import com.ndroid.shopping.shopping_api.model.UserModel;
import com.ndroid.shopping.shopping_api.repository.UserRepository;


@Service
public class UserService {

    @Autowired
    private final UserRepository repo;

     public UserService(UserRepository registrationRepository){
        this.repo = registrationRepository;
     }
}

// @Configuration
// @EnableWebSecurity
// @RequiredArgsConstructor
// public class SecurityConfig{
//     private final UserService service;
//     public SecurityfilterChain SecurityfilterChain(){
//         http

//     }
// }

