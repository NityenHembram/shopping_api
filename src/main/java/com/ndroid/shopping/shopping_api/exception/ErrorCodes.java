package com.ndroid.shopping.shopping_api.exception;

/**
 * Constants for error codes used throughout the application
 */
public final class ErrorCodes {

    // Authentication & Authorization
    public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String INVALID_TOKEN_TYPE = "INVALID_TOKEN_TYPE";
    public static final String MISSING_AUTH_HEADER = "MISSING_AUTH_HEADER";

    // Validation
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String CONSTRAINT_VIOLATION = "CONSTRAINT_VIOLATION";
    public static final String BIND_ERROR = "BIND_ERROR";
    public static final String MISSING_PARAMETER = "MISSING_PARAMETER";
    public static final String TYPE_MISMATCH = "TYPE_MISMATCH";
    public static final String MALFORMED_REQUEST = "MALFORMED_REQUEST";

    // Resource Management
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";

    // Business Logic
    public static final String BUSINESS_ERROR = "BUSINESS_ERROR";
    public static final String DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    public static final String INSUFFICIENT_PERMISSIONS = "INSUFFICIENT_PERMISSIONS";

    // HTTP & Network
    public static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
    public static final String UNSUPPORTED_MEDIA_TYPE = "UNSUPPORTED_MEDIA_TYPE";
    public static final String ENDPOINT_NOT_FOUND = "ENDPOINT_NOT_FOUND";
    public static final String FILE_SIZE_EXCEEDED = "FILE_SIZE_EXCEEDED";

    // Database & System
    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    public static final String DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    private ErrorCodes() {
        // Prevent instantiation
    }
}