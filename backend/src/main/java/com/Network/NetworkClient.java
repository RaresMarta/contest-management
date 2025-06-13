package com.Network;

import Network.UpdateMessage;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class NetworkClient {
    private Socket socket;

    public void start(String host, int port, Consumer<UpdateMessage> onReceived) {
        try {
            socket = new Socket(host, port);
            socket.setTcpNoDelay(true);

            new Thread(() -> {
                try {
                    UpdateMessage msg;
                    while ((msg = UpdateMessage.parseDelimitedFrom(socket.getInputStream())) != null) {
                        onReceived.accept(msg);
                    }
                } catch (IOException ignored) {}
            }).start();
            System.out.println("Connected to server");
        } catch (IOException ex) {
            System.err.println("Cannot connect: " + ex.getMessage());
        }
    }

    public void send(String type, String payload) {
        try {
            UpdateMessage.newBuilder()
                    .setType(type)
                    .setPayload(payload)
                    .build()
                    .writeDelimitedTo(socket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Send failed: " + ex.getMessage());
        }
    }
}

