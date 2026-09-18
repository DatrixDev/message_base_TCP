package com.datrixdev.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatClient(String serverIp) throws IOException {
        socket = new Socket(serverIp, DirectoryServer.PORT);

        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String register(String username, int peerPort) throws IOException {
        out.println("REGISTER|" + username + "|" + peerPort);
        return in.readLine();
    }

    public String getOnlineUsers() throws IOException {
        out.println("LIST");
        return in.readLine();
    }

    public String findUser(String username) throws IOException {
        out.println("FIND|" + username);
        return in.readLine();
    }

    public void disconnect() throws IOException {
        out.println("BYE");
        socket.close();
    }
}