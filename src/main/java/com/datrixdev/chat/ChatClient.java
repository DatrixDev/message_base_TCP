package com.datrixdev.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private BlockingQueue<String> replies =
            new LinkedBlockingQueue<>();

    private Consumer<String> onlineUsersListener;

    public ChatClient(String serverIp) throws IOException {
        socket = new Socket(serverIp, DirectoryServer.PORT);

        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(this::listenServer).start();
    }

    private void listenServer() {
        try {
            String response;

            while ((response = in.readLine()) != null) {
                if (response.startsWith("USERS|")) {
                    if (onlineUsersListener != null) {
                        onlineUsersListener.accept(response);
                    }
                } else {
                    replies.offer(response);
                }
            }

        } catch (IOException e) {
            System.out.println("Da mat ket noi Server");
        }
    }

    public void setOnlineUsersListener(
            Consumer<String> onlineUsersListener
    ) {
        this.onlineUsersListener = onlineUsersListener;
    }

    private String waitReply() throws IOException {
        try {
            return replies.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Loi khi cho phan hoi Server");
        }
    }

    public synchronized String register(
            String username,
            int peerPort
    ) throws IOException {
        out.println("REGISTER|" + username + "|" + peerPort);
        return waitReply();
    }

    public synchronized String findUser(String username)
            throws IOException {
        out.println("FIND|" + username);
        return waitReply();
    }

    public synchronized void requestOnlineUsers() {
        out.println("LIST");
    }

    public synchronized void disconnect() throws IOException {
        out.println("BYE");
        socket.close();
    }
}