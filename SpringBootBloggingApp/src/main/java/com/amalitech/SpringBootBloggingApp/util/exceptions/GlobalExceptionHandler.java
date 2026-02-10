package com.amalitech.SpringBootBloggingApp.util.exceptions;

import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handling: maps exceptions to the same ApiResponse envelope
 * (status, message, data) so all API errors return a consistent shape.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Validation and business rule violations (e.g. invalid ID, duplicate email).
     */
    @ExceptionHandler(UserInputsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserInputsException(UserInputsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Bean Validation failures on request DTOs (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message.isEmpty() ? "Validation failed" : message));
    }

    /**
     * Catch-all for unexpected errors; returns 500 with a generic message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred"));
    }
}
