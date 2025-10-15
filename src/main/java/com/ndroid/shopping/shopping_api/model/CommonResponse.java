
package com.ndroid.shopping.shopping_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    public int statusCode;
    private String message;
    private Object data;
}
