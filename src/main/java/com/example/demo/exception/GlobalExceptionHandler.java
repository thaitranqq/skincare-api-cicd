package com.example.demo.exception;

import com.example.demo.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        // Ghi log lỗi để gỡ lỗi
        logger.error("Caught RuntimeException: {}", ex.getMessage());

        // Xử lý các trường hợp lỗi cụ thể
        String message = ex.getMessage();

        if (message.contains("pending verification")) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403 Forbidden
                    .body(ApiResponse.fail("ACCOUNT_PENDING", message));
        }

        if (message.contains("Invalid credentials")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                    .body(ApiResponse.fail("INVALID_CREDENTIALS", message));
        }

        if (message.contains("already registered")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 Conflict
                    .body(ApiResponse.fail("EMAIL_IN_USE", message));
        }

        if (message.contains("token has expired")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400 Bad Request
                    .body(ApiResponse.fail("TOKEN_EXPIRED", message));
        }

        if (message.contains("Invalid verification token")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400 Bad Request
                    .body(ApiResponse.fail("INVALID_TOKEN", message));
        }

        // Xử lý các lỗi RuntimeException chung khác
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal Server Error
                .body(ApiResponse.fail("INTERNAL_SERVER_ERROR", "An unexpected error occurred: " + message));
    }
}
