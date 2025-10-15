package com.ndroid.shopping.shopping_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ndroid.shopping.shopping_api.dto.ErrorResponseDto;
import com.ndroid.shopping.shopping_api.model.UserModel;
import com.ndroid.shopping.shopping_api.repository.UserRepository;
import com.ndroid.shopping.shopping_api.utils.AuthUtils;

import java.util.ArrayList;
import java.util.List;
import io.jsonwebtoken.lang.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUtils authUtils;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // List of public endpoints that don't require authentication
    private final List<String> publicPaths = new ArrayList<>(
            java.util.Arrays.asList(
                    "/auth/",
                    "/swagger-ui/",
                    "/v3/api-docs/",
                    "/swagger-ui.html",
                    "/h2-console/")
    );

    public JwtAuthenticationFilter(AuthUtils authUtils, UserRepository userRepository) {
        this.authUtils = authUtils;
        this.userRepository = userRepository;
        // Configure ObjectMapper to handle LocalDateTime
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode, String errorCode)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .status(statusCode)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("Error serializing error response", e);
            // Fallback to simple JSON without timestamp
            String fallbackJson = String.format(
                    "{\"success\":false,\"errorCode\":\"%s\",\"message\":\"%s\",\"status\":%d}",
                    errorCode, message.replace("\"", "\\\""), statusCode);
            response.getWriter().write(fallbackJson);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;

        try {

            log.info("------------request-----------");
            log.info("Method: {}", request.getMethod());
            log.info("URL: {}", request.getRequestURL());
            log.info("Path: {}", request.getServletPath());
            log.info("Authorization Header: {}", authHeader);
            log.info("Remote Address: {}", request.getRemoteAddr());


            if(publicPaths.stream().anyMatch(path -> request.getServletPath().startsWith(path))) {
                log.info("Public path accessed, skipping authentication");
                filterChain.doFilter(request, response);
                return;
            }

            // Safely extract token
            try {
                String[] parts = authHeader.split("Bearer ");
                if (parts.length >= 2 && parts[1] != null && !parts[1].trim().isEmpty()) {
                    token = parts[1].trim();
                } else {
                    log.warn("Invalid Bearer token format");
                    sendErrorResponse(response, "Invalid Bearer token format",
                            HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN_FORMAT");
                    return;
                }
            } catch (Exception e) {
                log.error("Error parsing Authorization header: {}", e.getMessage());
                sendErrorResponse(response, "Invalid Authorization header format",
                        HttpServletResponse.SC_UNAUTHORIZED, "INVALID_AUTH_FORMAT");
                return;
            }

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // For protected endpoints, send error response instead of just filtering
                sendErrorResponse(response, "Authorization header is missing or invalid",
                        HttpServletResponse.SC_UNAUTHORIZED, "MISSING_AUTH_HEADER");
                return;
            }
            // Check if token is expired
            if (authUtils.isTokenExpired(token)) {
                log.warn("Token is expired");
                sendErrorResponse(response, "Token is expired", HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_EXPIRED");
                return;
            }

            if (authUtils.extractTokenType(token) == null || !authUtils.extractTokenType(token).equals("access")) {
                log.warn("Not an access token");
                sendErrorResponse(response, "Not an access token", HttpServletResponse.SC_UNAUTHORIZED,
                        "INVALID_TOKEN_TYPE");
                return;
            }

            Integer userId = authUtils.extractUserId(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserModel userDetails = userRepository.findById(userId).orElse(null);
                if (userDetails == null) {
                    sendErrorResponse(response, "User not found", HttpServletResponse.SC_UNAUTHORIZED,
                            "USER_NOT_FOUND");
                    return;
                }

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT Authentication error: {}", e.getMessage());
            sendErrorResponse(response, "Invalid token: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED,
                    "INVALID_TOKEN");
        }
    }
}
