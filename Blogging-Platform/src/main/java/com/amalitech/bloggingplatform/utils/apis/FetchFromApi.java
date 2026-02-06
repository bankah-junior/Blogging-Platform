package com.amalitech.bloggingplatform.utils.apis;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FetchFromApi {

    public static ApiResponse fetchFromApi(
            String apiUrl,
            String method,
            String requestBody
    ) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.toUpperCase());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            if (hasRequestBody(method) && requestBody != null && !requestBody.isEmpty()) {
                connection.setDoOutput(true);
                try (var outputStream = connection.getOutputStream()) {
                    outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
                }
            }

            int responseCode = connection.getResponseCode();
            InputStream stream = (responseCode >= 200 && responseCode < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            String response = readResponse(stream);

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

            String status = jsonResponse != null && jsonResponse.has("status")
                    ? jsonResponse.get("status").getAsString()
                    : "error";
            String message = jsonResponse != null && jsonResponse.has("message")
                    ? jsonResponse.get("message").getAsString()
                    : "";
            JsonElement data = jsonResponse != null && jsonResponse.has("data")
                    ? jsonResponse.get("data")
                    : null;

            return new ApiResponse(status, message, data);

        } catch (Exception e) {
            return new ApiResponse("error", e.getMessage(), null);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    private static boolean hasRequestBody(String method) {
        return switch (method.toUpperCase()) {
            case "POST", "PUT", "PATCH" -> true;
            default -> false;
        };
    }

    private static String readResponse(InputStream stream) throws IOException {
        if (stream == null) return "";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
