package com.ndroid.shopping.shopping_api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails
 */
public class AuthenticationException extends BaseException {

    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_FAILED", HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, "AUTHENTICATION_FAILED", HttpStatus.UNAUTHORIZED);
    }
}