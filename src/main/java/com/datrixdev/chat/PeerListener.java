package com.datrixdev.chat;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
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

                System.out.println(
                        "[P2P-ACCEPT] Connection from "
                                + socket.getInetAddress().getHostAddress()
                                + ":" + socket.getPort()
                );

                new Thread(() -> handlePeer(socket)).start();
            }

        } catch (IOException e) {
            System.out.println("PeerListener da dung");
        }
    }

    private void handlePeer(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(
                    socket.getInputStream()
            );

            String type = in.readUTF();

            if (type.equals("MESSAGE")) {
                String from = in.readUTF();
                String message = in.readUTF();

                if (eventListener != null) {
                    eventListener.onMessageReceived(from, message);
                }

            } else if (type.equals("FILE")) {
                String from = in.readUTF();
                String fileName = in.readUTF();
                long fileSize = in.readLong();

                File folder = new File("received_files");
                folder.mkdirs();

                File savedFile = new File(
                        folder,
                        new File(fileName).getName()
                );

                FileOutputStream fileOut =
                        new FileOutputStream(savedFile);

                byte[] buffer = new byte[4096];
                long totalRead = 0;

                while (totalRead < fileSize) {
                    int bytesRead = in.read(
                            buffer,
                            0,
                            (int) Math.min(
                                    buffer.length,
                                    fileSize - totalRead
                            )
                    );

                    if (bytesRead == -1) {
                        break;
                    }

                    fileOut.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }

                fileOut.close();

                if (eventListener != null) {
                    eventListener.onFileReceived(
                            from,
                            fileName,
                            savedFile
                    );
                }
            }

            socket.close();

        } catch (IOException e) {
            System.out.println("Loi nhan du lieu P2P");
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