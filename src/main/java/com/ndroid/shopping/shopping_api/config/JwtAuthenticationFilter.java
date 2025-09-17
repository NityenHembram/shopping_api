package com.ndroid.shopping.shopping_api.config;

import aj.org.objectweb.asm.Handle;
import com.ndroid.shopping.shopping_api.model.UserModel;
import com.ndroid.shopping.shopping_api.repository.UserRepository;
import com.ndroid.shopping.shopping_api.service.UserDetailServiceImpl;
import com.ndroid.shopping.shopping_api.service.UserService;
import com.ndroid.shopping.shopping_api.utils.AuthUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUtils authUtils;
    private final UserDetailServiceImpl userDetailService;
    private final UserRepository userRepository;

    final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userName;

        try {

            log.info("------------request-----------");
            log.info("Method: {}", request.getMethod());
            log.info("URL: {}", request.getRequestURL());
            log.info("Path: {}", request.getServletPath());
            log.info("Authorization Header: {}", authHeader);
            log.info("Remote Address: {}", request.getRemoteAddr());

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            token = authHeader.split("Bearer ")[1];
            userName = authUtils.extractUserName(token);

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserModel userDetails = userRepository.findByUsername(userName).orElseThrow();

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);
        }catch (Exception e)
        {
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }
}
