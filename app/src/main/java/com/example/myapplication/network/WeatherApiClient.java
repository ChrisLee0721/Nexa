package com.example.myapplication.network;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WeatherApiClient {

    public WeatherResponse getCurrentWeather(double latitude, double longitude) throws IOException {
        String endpoint = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude
                + "&longitude=" + longitude
                + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m";

        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);

        int statusCode = connection.getResponseCode();
        String body = readResponseBody(statusCode >= 200 && statusCode < 300
                ? connection.getInputStream()
                : connection.getErrorStream());

        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("Weather API error " + statusCode + ": " + body);
        }

        try {
            JSONObject current = new JSONObject(body).getJSONObject("current");
            return new WeatherResponse(
                    current.optDouble("temperature_2m", Double.NaN),
                    current.optDouble("relative_humidity_2m", Double.NaN),
                    current.optDouble("wind_speed_10m", Double.NaN)
            );
        } catch (Exception e) {
            throw new IOException("Weather parse failed: " + e.getMessage(), e);
        }
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

    public static class WeatherResponse {
        public final double temperature;
        public final double humidity;
        public final double windSpeed;

        public WeatherResponse(double temperature, double humidity, double windSpeed) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
        }
    }
}

