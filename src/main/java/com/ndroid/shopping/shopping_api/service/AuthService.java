package com.ndroid.shopping.shopping_api.service;

import com.ndroid.shopping.shopping_api.dto.CommonResponse;
import com.ndroid.shopping.shopping_api.dto.LoginRequestDto;
import com.ndroid.shopping.shopping_api.dto.LoginResponseDto;
import com.ndroid.shopping.shopping_api.dto.UserRequestDto;
import com.ndroid.shopping.shopping_api.model.User;
import com.ndroid.shopping.shopping_api.repository.UserRepository;
import com.ndroid.shopping.shopping_api.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtils authUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public ResponseEntity<Object> login(LoginRequestDto loginRequestDto) {

        CommonResponse commonResponse;
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),
                            loginRequestDto.password));
            User userModel = (User) authentication.getPrincipal();
            Long userId = userModel.getId();

            String accessToken = authUtils.generateAccesssToken(userId.intValue());
            String refreshToken = authUtils.generateRefreshToken(userId.intValue());
            LoginResponseDto loginResponseDto = new LoginResponseDto(accessToken, refreshToken, userId);
            commonResponse = new CommonResponse(200, "Successfully Logged in", loginResponseDto);

            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
        } catch (BadCredentialsException e) {
            commonResponse = new CommonResponse(200, "Invalide Crednetials", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonResponse);
        }

    }

    public ResponseEntity<Object> refreshToken(String refreshToken) {
        CommonResponse commonResponse;
        try {
            if (authUtils.isTokenExpired(refreshToken)) {
                commonResponse = new CommonResponse(401, "Refresh Token is expired", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonResponse);
            }

            if (authUtils.extractTokenType(refreshToken) == "access") {
                commonResponse = new CommonResponse(400, "Invalid Token Type", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonResponse);
            }

            Integer userId = authUtils.extractUserId(refreshToken);
            String newAccessToken = authUtils.generateAccesssToken(userId);
            String newRefreshToken = authUtils.generateRefreshToken(userId);
            LoginResponseDto loginResponseDto = new LoginResponseDto(newAccessToken, newRefreshToken, userId);
            commonResponse = new CommonResponse(200, "Token refreshed successfully", loginResponseDto);
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            commonResponse = new CommonResponse(500, "Internal server error: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonResponse);
        }
    }

    public ResponseEntity<CommonResponse> registerUser(UserRequestDto userDto) {

        CommonResponse commonResponse = new CommonResponse();
        try {
            User user = userRepository.findByEmail(userDto.getUsername()).orElse(null);
            if (user != null)
                throw new IllegalArgumentException("User Already Exist");
            user = User.builder().email(userDto.getEmail())
                    .name(userDto.getUsername())
                    .password(encodePassword(userDto.getPassword()))
                    .phone(userDto.getPhone())
                    .createdAt(userDto.getCreatedAt()).build();
            userRepository.save(user);

            commonResponse.setStatusCode(HttpStatus.OK.value());
            commonResponse.setMessage("Success");
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);

        } catch (IllegalArgumentException e) {
            commonResponse.setStatusCode(HttpStatus.CONFLICT.value());
            commonResponse.setMessage("Data Conflict: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(commonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            commonResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            commonResponse.setMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commonResponse);
        }
    }

    public String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

}
