package com.ndroid.shopping.shopping_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for field validation errors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDto {

    @JsonProperty("field")
    private String field;

    @JsonProperty("rejected_value")
    private Object rejectedValue;

    @JsonProperty("message")
    private String message;

    @JsonProperty("code")
    private String code;

    public FieldErrorDto(String field, String message) {
        this.field = field;
        this.message = message;
    }
}