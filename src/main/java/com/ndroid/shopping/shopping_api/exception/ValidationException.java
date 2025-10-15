package com.ndroid.shopping.shopping_api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when request validation fails
 */
public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String message, Object details) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, details);
    }

    public ValidationException(String message, String errorCode, Object details) {
        super(message, errorCode, HttpStatus.BAD_REQUEST, details);
    }
}