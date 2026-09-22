package com.datrixdev.chat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class PeerSender {

    public static void sendMessage(
            String ip,
            int port,
            String from,
            String message
    ) throws IOException {

        System.out.println(
                "[P2P-SEND] " + from +
                        " -> " + ip + ":" + port +
                        " | MESSAGE: " + message
        );

        Socket socket = new Socket(ip, port);

        System.out.println(
                "[P2P-SEND] Connected successfully to "
                        + ip + ":" + port
        );

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());

        out.writeUTF("MESSAGE");
        out.writeUTF(from);
        out.writeUTF(message);

        out.close();
        socket.close();
    }

    public static void sendFile(
            String ip,
            int port,
            String from,
            File file
    ) throws IOException {

        System.out.println(
                "[P2P-SEND-FILE] " + from +
                        " -> " + ip + ":" + port +
                        " | FILE: " + file.getName() +
                        " | SIZE: " + file.length() + " bytes"
        );

        Socket socket = new Socket(ip, port);

        System.out.println(
                "[P2P-SEND-FILE] Connected successfully to "
                        + ip + ":" + port
        );

        DataOutputStream out = new DataOutputStream(
                socket.getOutputStream()
        );

        FileInputStream fileIn = new FileInputStream(file);

        out.writeUTF("FILE");
        out.writeUTF(from);
        out.writeUTF(file.getName());
        out.writeLong(file.length());

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = fileIn.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }

        fileIn.close();
        out.close();
        socket.close();
    }
}