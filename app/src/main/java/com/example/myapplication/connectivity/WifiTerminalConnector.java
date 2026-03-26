package com.example.myapplication.connectivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WifiTerminalConnector implements TerminalConnector {
    private final String host;
    private final int port;
    private Socket socket;

    public WifiTerminalConnector(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 5000);
    }

    @Override
    public void send(String payload) throws IOException {
        if (socket == null || !socket.isConnected()) {
            throw new IOException("Wi-Fi terminal is not connected");
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

