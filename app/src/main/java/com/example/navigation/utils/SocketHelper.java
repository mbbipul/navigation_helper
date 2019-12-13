package com.example.navigation.utils;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketHelper {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.0.8:8080/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
