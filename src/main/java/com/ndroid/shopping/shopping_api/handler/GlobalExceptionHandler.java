package com.ndroid.shopping.shopping_api.handler;

import com.ndroid.shopping.shopping_api.dto.ErrorResponseDto;
import com.ndroid.shopping.shopping_api.dto.FieldErrorDto;
import com.ndroid.shopping.shopping_api.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private ErrorResponseDto buildErrorResponse(String errorCode, String message, HttpStatus status,
            Object details, HttpServletRequest request) {
        return ErrorResponseDto.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .status(status.value())
                .details(details)
                .timestamp(LocalDateTime.now())
                .path(request != null ? request.getRequestURI() : null)
                .method(request != null ? request.getMethod() : null)
                .traceId(generateTraceId())
                .build();
    }

    // Handle custom base exceptions
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDto> handleBaseException(BaseException ex, HttpServletRequest request) {
        log.error("Base exception occurred: {}", ex.getMessage(), ex);

        ErrorResponseDto errorResponse = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getHttpStatus(),
                ex.getDetails(),
                request);

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getHttpStatus(),
                ex.getDetails(),
                request);

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    // Handle business exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Business exception: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getHttpStatus(),
                ex.getDetails(),
                request);

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    // Handle authentication exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(AuthenticationException ex,
            HttpServletRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getHttpStatus(),
                ex.getDetails(),
                request);

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    // Handle authorization exceptions
    @ExceptionHandler({ AuthorizationException.class, AccessDeniedException.class })
    public ResponseEntity<ErrorResponseDto> handleAuthorizationException(Exception ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                "ACCESS_DENIED",
                "You don't have permission to access this resource",
                HttpStatus.FORBIDDEN,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Handle validation exceptions (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<FieldErrorDto> fieldErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new FieldErrorDto(
                    error.getField(),
                    error.getRejectedValue(),
                    error.getDefaultMessage(),
                    error.getCode()));
        }

        ErrorResponseDto errorResponse = buildErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed for one or more fields",
                HttpStatus.BAD_REQUEST,
                fieldErrors,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle bind exceptions
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(BindException ex, HttpServletRequest request) {
        log.warn("Bind exception: {}", ex.getMessage());

        List<FieldErrorDto> fieldErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new FieldErrorDto(error.getField(), error.getDefaultMessage()));
        }

        ErrorResponseDto errorResponse = buildErrorResponse(
                "BIND_ERROR",
                "Request binding failed",
                HttpStatus.BAD_REQUEST,
                fieldErrors,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle constraint violation exceptions
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex,
            HttpServletRequest request) {
        log.warn("Constraint violation: {}", ex.getMessage());

        List<FieldErrorDto> fieldErrors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            fieldErrors.add(new FieldErrorDto(
                    violation.getPropertyPath().toString(),
                    violation.getInvalidValue(),
                    violation.getMessage(),
                    violation.getMessageTemplate()));
        }

        ErrorResponseDto errorResponse = buildErrorResponse(
                "CONSTRAINT_VIOLATION",
                "Constraint validation failed",
                HttpStatus.BAD_REQUEST,
                fieldErrors,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle missing request parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingParameterException(MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        log.warn("Missing request parameter: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                "MISSING_PARAMETER",
                String.format("Required parameter '%s' of type '%s' is missing", ex.getParameterName(),
                        ex.getParameterType()),
                HttpStatus.BAD_REQUEST,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle method argument type mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatchException(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        log.warn("Type mismatch: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                "TYPE_MISMATCH",
                String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                        ex.getValue(), ex.getName(),
                        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"),
                HttpStatus.BAD_REQUEST,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle HTTP message not readable
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        log.warn("HTTP message not readable: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                "MALFORMED_REQUEST",
                "Request body is malformed or missing",
                HttpStatus.BAD_REQUEST,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle HTTP method not supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        log.warn("Method not supported: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                "METHOD_NOT_ALLOWED",
                String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                        ex.getMethod(), ex.getSupportedHttpMethods()),
                HttpStatus.METHOD_NOT_ALLOWED,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handle HTTP media type not supported
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {
        log.warn("Media type not supported: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                "UNSUPPORTED_MEDIA_TYPE",
                String.format("Media type '%s' is not supported. Supported types: %s",
                        ex.getContentType(), ex.getSupportedMediaTypes()),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // Handle file upload size exceeded
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
            HttpServletRequest request) {
        log.warn("Upload size exceeded: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                "FILE_SIZE_EXCEEDED",
                "Maximum upload file size exceeded",
                HttpStatus.PAYLOAD_TOO_LARGE,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    // Handle no handler found (404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoHandlerFoundException(NoHandlerFoundException ex,
            HttpServletRequest request) {
        log.warn("No handler found: {}", ex.getMessage());

        ErrorResponseDto errorResponse = buildErrorResponse(
                "ENDPOINT_NOT_FOUND",
                String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
                HttpStatus.NOT_FOUND,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Handle database exceptions
    @ExceptionHandler({ DataAccessException.class, SQLException.class })
    public ResponseEntity<ErrorResponseDto> handleDatabaseException(Exception ex, HttpServletRequest request) {
        log.error("Database error occurred: {}", ex.getMessage(), ex);

        ErrorResponseDto errorResponse = buildErrorResponse(
                "DATABASE_ERROR",
                "A database error occurred. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle data integrity violation
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);

        String message = "Data integrity constraint violation";
        if (ex.getMessage() != null && ex.getMessage().contains("duplicate")) {
            message = "Duplicate entry. Resource already exists.";
        }

        ErrorResponseDto errorResponse = buildErrorResponse(
                "DATA_INTEGRITY_VIOLATION",
                message,
                HttpStatus.CONFLICT,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorResponseDto errorResponse = buildErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                null,
                request);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
