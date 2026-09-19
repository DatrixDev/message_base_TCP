package com.datrixdev.chat;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerListener extends Thread {
    private ServerSocket serverSocket;
    private PeerEventListener eventListener;

    public PeerListener() throws IOException {
        serverSocket = new ServerSocket(0);
        start();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setEventListener(PeerEventListener eventListener) {
        this.eventListener = eventListener;

    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handlePeer(socket)).start();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void handlePeer(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String type = in.readUTF();
            if (type.equals("MESSAGE")) {
                String form = in.readUTF();
                String message = in.readUTF();
                if (eventListener != null) {
                    eventListener.onMessageReceived(form, message);
                }
            }
        } catch (
                IOException ex
        ) {
            System.out.println("Loi");
        }
    }

    public void stopListener() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
