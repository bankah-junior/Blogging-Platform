package com.amalitech.SpringBootBloggingApp.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Structured API response envelope with status, message, and optional data.
 * Used by all controllers for consistent response shape.
 *
 * @param <T> type of the payload (data)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";

    private String status;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(STATUS_SUCCESS, null, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(STATUS_SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(STATUS_ERROR, message, null);
    }

    public static <T> ApiResponse<T> of(String status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
