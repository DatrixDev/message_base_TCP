package com.datrixdev.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DirectoryServer {
    public static final int PORT = 2006;

    public static final Map<String, String> onlineUsers =
            new ConcurrentHashMap<>();

    public static final Map<String, ClientHandler> onlineClients =
            new ConcurrentHashMap<>();

    public static void broadcastOnlineUsers() {
        String message = "USERS|" + String.join(
                ",",
                onlineUsers.keySet()
        );

        for (ClientHandler client : onlineClients.values()) {
            client.send(message);
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);

            System.out.println("Server waiting at port " + PORT);

            while (true) {
                Socket socket = server.accept();
                new ClientHandler(socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}