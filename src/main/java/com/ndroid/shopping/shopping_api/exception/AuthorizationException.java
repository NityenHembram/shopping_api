package com.ndroid.shopping.shopping_api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user doesn't have permission to access a resource
 */
public class AuthorizationException extends BaseException {

    public AuthorizationException(String message) {
        super(message, "ACCESS_DENIED", HttpStatus.FORBIDDEN);
    }

    public AuthorizationException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.FORBIDDEN);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause, "ACCESS_DENIED", HttpStatus.FORBIDDEN);
    }
}