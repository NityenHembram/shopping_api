package com.ndroid.shopping.shopping_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ndroid.shopping.shopping_api.dto.ErrorResponseDto;
import com.ndroid.shopping.shopping_api.model.User;
import com.ndroid.shopping.shopping_api.repository.UserRepository;
import com.ndroid.shopping.shopping_api.utils.AuthUtils;

import java.util.List;

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
    private final List<String> publicPaths = java.util.Arrays.asList(
            "/auth",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-ui.html",
            "/h2-console",
            "/swagger-resources",
            "/webjars");

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

        try {
            log.info("------------request-----------");
            log.info("Method: {}", request.getMethod());
            log.info("URL: {}", request.getRequestURL());
            log.info("Path: {}", request.getServletPath());
            log.info("Remote Address: {}", request.getRemoteAddr());

            // Check if this is a public path first
            String servletPath = request.getServletPath();
            log.info("Checking path: {} against public paths: {}", servletPath, publicPaths);

            boolean isPublicPath = publicPaths.stream().anyMatch(path -> {
                boolean matches = servletPath.equals(path) || servletPath.startsWith(path + "/");
                log.debug("Path check: {} matches {}: {}", servletPath, path, matches);
                return matches;
            });

            log.info("Is public path: {}", isPublicPath);

            if (isPublicPath) {
                log.info("Public path accessed: {}, skipping authentication", servletPath);
                filterChain.doFilter(request, response);
                return;
            } // For protected endpoints, check authorization header
            final String authHeader = request.getHeader("Authorization");
            log.info("Authorization Header: {}", authHeader != null ? "Bearer ***" : "null");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Authorization header is missing or invalid");
                sendErrorResponse(response, "Authorization header is missing or invalid",
                        HttpServletResponse.SC_UNAUTHORIZED, "MISSING_AUTH_HEADER");
                return;
            }

            // Extract token
            final String token;
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

            // Check if token is expired
            if (authUtils.isTokenExpired(token)) {
                log.warn("Token is expired");
                sendErrorResponse(response, "Token is expired", HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_EXPIRED");
                return;
            }

            // Check token type
            if (authUtils.extractTokenType(token) == null || !authUtils.extractTokenType(token).equals("access")) {
                log.warn("Not an access token");
                sendErrorResponse(response, "Not an access token", HttpServletResponse.SC_UNAUTHORIZED,
                        "INVALID_TOKEN_TYPE");
                return;
            }

            // Extract user and set authentication
            Integer userId = authUtils.extractUserId(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = userRepository.findById(Long.valueOf(userId)).orElse(null);
                if (userDetails == null) {
                    log.warn("User not found for ID: {}", userId);
                    sendErrorResponse(response, "User not found", HttpServletResponse.SC_UNAUTHORIZED,
                            "USER_NOT_FOUND");
                    return;
                }

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                log.info("User authenticated successfully: {}", userDetails.getEmail());
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT Authentication error: {}", e.getMessage(), e);
            sendErrorResponse(response, "Authentication failed: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED,
                    "AUTHENTICATION_ERROR");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        log.info("ShouldNotFilter check for path: {}", path);

        boolean shouldSkip = publicPaths.stream().anyMatch(publicPath -> {
            boolean matches = path.equals(publicPath) || path.startsWith(publicPath + "/");
            log.debug("ShouldNotFilter: {} matches {}: {}", path, publicPath, matches);
            return matches;
        });

        log.info("ShouldNotFilter result: {}", shouldSkip);
        return shouldSkip;
    }
}
