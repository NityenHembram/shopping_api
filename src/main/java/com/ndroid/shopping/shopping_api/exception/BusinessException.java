package com.ndroid.shopping.shopping_api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when business logic validation fails
 */
public class BusinessException extends BaseException {

    public BusinessException(String message) {
        super(message, "BUSINESS_ERROR", HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message, String errorCode, Object details) {
        super(message, errorCode, HttpStatus.BAD_REQUEST, details);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause, "BUSINESS_ERROR", HttpStatus.BAD_REQUEST);
    }
}