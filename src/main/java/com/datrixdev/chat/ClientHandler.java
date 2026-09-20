package com.datrixdev.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;

        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        out = new PrintWriter(socket.getOutputStream(), true);

        start();
    }

    public synchronized void send(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            String message = in.readLine();

            while (message != null && !message.equals("BYE")) {
                String[] parts = message.split("\\|");

                if (parts[0].equals("REGISTER") && parts.length == 3) {
                    username = parts[1];

                    String ip = socket.getInetAddress().getHostAddress();
                    String peerPort = parts[2];

                    DirectoryServer.onlineUsers.put(
                            username,
                            ip + ":" + peerPort
                    );

                    DirectoryServer.onlineClients.put(
                            username,
                            this
                    );

                    System.out.println(username + " online");

                    send("SUCCESS|Dang nhap thanh cong");

                    DirectoryServer.broadcastOnlineUsers();

                } else if (parts[0].equals("FIND") && parts.length == 2) {
                    String peerAddress =
                            DirectoryServer.onlineUsers.get(parts[1]);

                    if (peerAddress == null) {
                        send("ERROR|Nguoi dung khong online");
                    } else {
                        send("PEER|" + parts[1]
                                + "|" + peerAddress);
                    }

                } else if (parts[0].equals("LIST")) {
                    DirectoryServer.broadcastOnlineUsers();

                } else {
                    send("ERROR|Lenh khong hop le");
                }

                message = in.readLine();
            }

        } catch (IOException e) {
            System.out.println("Mot client da ngat ket noi");

        } finally {
            if (username != null) {
                boolean removed = DirectoryServer.onlineClients.remove(
                        username,
                        this
                );

                if (removed) {
                    DirectoryServer.onlineUsers.remove(username);
                    System.out.println(username + " offline");

                    DirectoryServer.broadcastOnlineUsers();
                }
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}