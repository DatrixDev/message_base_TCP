package com.datrixdev.chat;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DirectoryServer {

    public static final int PORT = 2006;
    public static final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Server waiting at port " + PORT);
            while (true) {
                Socket socket = server.accept();
                new ClientHandler(socket);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
