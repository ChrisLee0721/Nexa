package com.example.myapplication.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BluetoothTerminalConnector implements TerminalConnector {
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final String macAddress;
    private BluetoothSocket socket;

    public BluetoothTerminalConnector(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public void connect() throws IOException {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            throw new IOException("Bluetooth is not supported on this device");
        }
        BluetoothDevice device = adapter.getRemoteDevice(macAddress);
        socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
        socket.connect();
    }

    @Override
    public void send(String payload) throws IOException {
        if (socket == null || !socket.isConnected()) {
            throw new IOException("Bluetooth terminal is not connected");
        }
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write((payload + "\n").getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    @Override
    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
                // No-op.
            }
        }
    }
}

