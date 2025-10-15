package com.ndroid.shopping.shopping_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized error response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    @JsonProperty("success")
    @Builder.Default
    private boolean success = false;

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("details")
    private Object details;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    @JsonProperty("path")
    private String path;

    @JsonProperty("method")
    private String method;

    @JsonProperty("status")
    private int status;

    @JsonProperty("trace_id")
    private String traceId;

    // Helper method to create error response
    public static ErrorResponseDto of(String errorCode, String message, int status) {
        return ErrorResponseDto.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Helper method to create error response with details
    public static ErrorResponseDto of(String errorCode, String message, int status, Object details) {
        return ErrorResponseDto.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .status(status)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
    }
}