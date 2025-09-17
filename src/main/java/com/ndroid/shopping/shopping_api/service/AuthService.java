package com.ndroid.shopping.shopping_api.service;


import com.ndroid.shopping.shopping_api.dto.LoginRequestDto;
import com.ndroid.shopping.shopping_api.dto.LoginResponseDto;
import com.ndroid.shopping.shopping_api.dto.UserRequestDto;
import com.ndroid.shopping.shopping_api.model.CommonResponse;
import com.ndroid.shopping.shopping_api.model.UserModel;
import com.ndroid.shopping.shopping_api.repository.UserRepository;
import com.ndroid.shopping.shopping_api.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final ModelMapper modelMapper;


    public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.username,
                loginRequestDto.password));
        UserModel userModel = (UserModel) authentication.getPrincipal();

        String token = authUtils.generateToken(userModel);
        CommonResponse commonResponse = new CommonResponse(200, "Successfully Logged in", null);
        LoginResponseDto loginResponseDto = new LoginResponseDto(token, null, commonResponse);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }


    public ResponseEntity<CommonResponse> registerUser(UserRequestDto userDto) {
        UserModel user = userRepository.findByUsername(userDto.getUsername()).orElse(null);
        if(user != null) throw new IllegalArgumentException("User Already Exist");
        CommonResponse commonResponse = new CommonResponse();
        try {
           user =  UserModel.builder().email(userDto.getEmail())
                    .username(userDto.getUsername())
                    .password(encodePassword(userDto.getPassword()))
                    .phone(userDto.getPhone())
                    .address(userDto.getAddress())
                    .createdAt(userDto.getCreatedAt()).build();
                    userRepository.save(user);

            commonResponse.setStatusCode(HttpStatus.OK.value());
            commonResponse.setMessage("Success");
            return ResponseEntity.status(HttpStatus.OK).body(commonResponse);

        } catch (DataIntegrityViolationException e) {
            commonResponse.setStatusCode(HttpStatus.CONFLICT.value());
            commonResponse.setMessage("Data Conflict: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonResponse);
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
