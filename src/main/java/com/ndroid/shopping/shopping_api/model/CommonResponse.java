
package com.ndroid.shopping.shopping_api.model;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    public int statusCode;
    private String message;
    @Nullable
    private Object data;
}
