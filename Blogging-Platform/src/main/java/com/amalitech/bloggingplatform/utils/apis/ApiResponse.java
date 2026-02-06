package com.amalitech.bloggingplatform.utils.apis;

import com.google.gson.JsonElement;

/**
 * Lightweight mirror of the Spring Boot ApiResponse envelope.
 *
 * <p>It intentionally keeps {@code data} as a raw {@link JsonElement} so that
 * callers (services) can deserialize it into the exact DTO / entity type they
 * need (single object, list, or paginated response).</p>
 */
public class ApiResponse {

    private final String status;
    private final String message;
    private final JsonElement data;

    public ApiResponse(String status, String message, JsonElement data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public JsonElement getData() {
        return data;
    }
}

