package com.example.myapplication.connectivity;

import java.io.IOException;

public interface TerminalConnector {
    void connect() throws IOException;

    void send(String payload) throws IOException;

    void close();
}

