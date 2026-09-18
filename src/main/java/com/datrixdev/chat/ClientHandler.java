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

                    System.out.println(username + " online");
                    out.println("SUCCESS|Dang nhap thanh cong");

                } else if (parts[0].equals("FIND") && parts.length == 2) {
                    String peerAddress =
                            DirectoryServer.onlineUsers.get(parts[1]);

                    if (peerAddress == null) {
                        out.println("ERROR|Nguoi dung khong online");
                    } else {
                        out.println("PEER|" + parts[1] + "|" + peerAddress);
                    }

                } else if (parts[0].equals("LIST")) {
                    out.println("USERS|" + String.join(
                            ",",
                            DirectoryServer.onlineUsers.keySet()
                    ));

                } else {
                    out.println("ERROR|Lenh khong hop le");
                }

                message = in.readLine();
            }

        } catch (IOException e) {
            System.out.println("Mot client da ngat ket noi");

        } finally {
            if (username != null) {
                DirectoryServer.onlineUsers.remove(username);
                System.out.println(username + " offline");
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}