package com.ndroid.shopping.shopping_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ndroid.shopping.shopping_api.dto.ApiResponseDto;
import com.ndroid.shopping.shopping_api.dto.CommonResponse;
import com.ndroid.shopping.shopping_api.dto.UserModelDto;
import com.ndroid.shopping.shopping_api.model.User;
import com.ndroid.shopping.shopping_api.repository.UserRepository;

@Service
public class UserService {

   @Autowired
   private final UserRepository repo;

   public UserService(UserRepository registrationRepository) {
      this.repo = registrationRepository;
   }

   public ResponseEntity<Object> getAllUserData() {

      try {
         List<User> usersData = repo.findAll();
         List<UserModelDto> userDto = usersData.stream().map(user -> {
            UserModelDto dto = new UserModelDto();
            dto.setId(user.getId());
            dto.setUsername(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setCreatedAt(user.getCreatedAt());
            return dto;
         }).collect(Collectors.toList());

         ApiResponseDto<Object> apiResponseDto = ApiResponseDto.builder()
               .success(true)
               .message("User data fetched successfully")
               .data(userDto)
               .timestamp(LocalDateTime.now())
               .status(HttpStatus.OK.value())
               .build();
         return ResponseEntity.status(HttpStatus.OK).body(apiResponseDto);
      } catch (DataIntegrityViolationException e) {
         ApiResponseDto<Object> apiResponseDto = ApiResponseDto.builder()
               .success(false)
               .message("Failed to fetch user data")
               .data(null)
               .timestamp(LocalDateTime.now())
               .status(HttpStatus.BAD_REQUEST.value())
               .build();
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDto);
      }
   }

   public ResponseEntity<CommonResponse> getUserById(Long id) {
      if (id == null) {
         return ResponseEntity.badRequest().build();
      }
      if (repo.findById(id).isEmpty()) {
         return ResponseEntity.notFound().build();
      }
      CommonResponse commonResponse = new CommonResponse();
      commonResponse.setMessage("User fetched successfully");
      commonResponse.setData(repo.findById(id).get());
      commonResponse.setStatusCode(200);
      return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
   }
}
