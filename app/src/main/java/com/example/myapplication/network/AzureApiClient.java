package com.example.myapplication.network;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AzureApiClient {

    public SyncResponse getTelemetry(String endpoint, String apiKey) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
        if (apiKey != null && !apiKey.isEmpty()) {
            connection.setRequestProperty("x-functions-key", apiKey);
        }

        int code = connection.getResponseCode();
        String responseBody = readResponseBody(code >= 200 && code < 300
                ? connection.getInputStream()
                : connection.getErrorStream());

        return new SyncResponse(code, responseBody);
    }

    public SyncResponse postTelemetry(String endpoint, String apiKey, JSONObject payload) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        if (apiKey != null && !apiKey.isEmpty()) {
            connection.setRequestProperty("x-functions-key", apiKey);
        }

        byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(body);
            outputStream.flush();
        }

        int code = connection.getResponseCode();
        String responseBody = readResponseBody(code >= 200 && code < 300
                ? connection.getInputStream()
                : connection.getErrorStream());

        return new SyncResponse(code, responseBody);
    }

    private String readResponseBody(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    public static class SyncResponse {
        public final int statusCode;
        public final String body;

        public SyncResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }
}

