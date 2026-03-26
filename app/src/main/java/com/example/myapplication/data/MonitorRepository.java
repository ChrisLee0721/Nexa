package com.example.myapplication.data;

import com.example.myapplication.connectivity.BluetoothTerminalConnector;
import com.example.myapplication.connectivity.TerminalConnector;
import com.example.myapplication.connectivity.WifiTerminalConnector;
import com.example.myapplication.network.AzureApiClient;
import com.example.myapplication.network.WeatherApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;

public class MonitorRepository {
    private final AzureApiClient azureApiClient;
    private final WeatherApiClient weatherApiClient;
    private TerminalConnector connector;

    public MonitorRepository(AzureApiClient azureApiClient, WeatherApiClient weatherApiClient) {
        this.azureApiClient = azureApiClient;
        this.weatherApiClient = weatherApiClient;
    }

    public void connectBluetooth(String macAddress) throws IOException {
        closeConnector();
        connector = new BluetoothTerminalConnector(macAddress);
        connector.connect();
    }

    public void connectWifi(String host, int port) throws IOException {
        closeConnector();
        connector = new WifiTerminalConnector(host, port);
        connector.connect();
    }

    public void sendToTerminal(String payload) throws IOException {
        if (connector == null) {
            throw new IOException("No terminal connection established");
        }
        connector.send(payload);
    }

    public SensorSnapshot getFromAzure(String endpoint, String apiKey) throws IOException {
        AzureApiClient.SyncResponse response = azureApiClient.getTelemetry(endpoint, apiKey);
        if (response.statusCode < 200 || response.statusCode >= 300) {
            throw new IOException("Azure response " + response.statusCode + ": " + response.body);
        }

        try {
            JSONObject json = new JSONObject(response.body);
            JSONObject payload = json.has("payload") && json.opt("payload") instanceof JSONObject
                    ? json.getJSONObject("payload")
                    : json;

            double temperature = payload.optDouble("temperature", Double.NaN);
            if (Double.isNaN(temperature)) {
                temperature = payload.optDouble("temperatureC", 0d);
            }

            double humidity = payload.optDouble("humidity", Double.NaN);
            if (Double.isNaN(humidity)) {
                humidity = payload.optDouble("humidityPercent", 0d);
            }

            return new SensorSnapshot(temperature, humidity, "azure");
        } catch (Exception e) {
            throw new IOException("Azure data parse failed: " + e.getMessage(), e);
        }
    }

    public SensorSnapshot getMockLocalSnapshot(String source) {
        double temperature = 22 + Math.random() * 10;
        double humidity = 40 + Math.random() * 35;
        return new SensorSnapshot(
                round(temperature),
                round(humidity),
                source
        );
    }

    public String getWeatherSummary(double latitude, double longitude) throws IOException {
        WeatherApiClient.WeatherResponse weather = weatherApiClient.getCurrentWeather(latitude, longitude);
        return String.format(
                Locale.US,
                "天气 %.1f°C, 湿度 %.0f%%, 风速 %.1f m/s",
                weather.temperature,
                weather.humidity,
                weather.windSpeed
        );
    }

    public AzureApiClient.SyncResponse syncToAzure(
            String endpoint,
            String apiKey,
            String mode,
            String target,
            String payload
    ) throws IOException, JSONException {
        JSONObject body = new JSONObject();
        body.put("timestamp", Instant.now().toString());
        body.put("mode", mode);
        body.put("target", target);
        body.put("payload", payload);
        return azureApiClient.postTelemetry(endpoint, apiKey, body);
    }

    public void closeConnector() {
        if (connector != null) {
            connector.close();
            connector = null;
        }
    }

    private double round(double value) {
        return Math.round(value * 10d) / 10d;
    }
}

