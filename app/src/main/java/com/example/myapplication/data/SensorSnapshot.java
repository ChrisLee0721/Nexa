package com.example.myapplication.data;

public class SensorSnapshot {
    public final double temperature;
    public final double humidity;
    public final String source;

    public SensorSnapshot(double temperature, double humidity, String source) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.source = source;
    }
}

