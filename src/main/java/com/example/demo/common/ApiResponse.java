package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Prevents sending null fields in the JSON response
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;
    private Object meta;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) { // Overloaded ok method with message
        return new ApiResponse<>(true, data, null, message);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, null, null, null);
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message), null);
    }

    /**
     * Creates a standardized error response with a default error code.
     * @param message The error message.
     * @return An ApiResponse object for failure.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, new ApiError("ERROR", message), null);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiError {
        private String code;
        private String message;
    }
}
