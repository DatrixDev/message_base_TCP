package com.datrixdev.chat;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("P2P Chat - Vo Quoc Dat");
        setSize(350, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel titleLabel = new JLabel(
                "P2P Chat & File",
                SwingConstants.CENTER
        );

        usernameField = new JTextField();

        JButton connectButton = new JButton("Kết nối");

        statusLabel = new JLabel(
                "Nhập biệt danh",
                SwingConstants.CENTER
        );

        panel.add(titleLabel);
        panel.add(usernameField);
        panel.add(connectButton);
        panel.add(statusLabel);

        add(panel);

        connectButton.addActionListener(e -> connectToServer());
    }

    private void connectToServer() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            statusLabel.setText("Vui long nhap ten");
            return;
        }

        new Thread(() -> {
            try {
                ChatClient chatClient = new ChatClient("127.0.0.1");
                PeerListener peerListener = new PeerListener();

                String response = chatClient.register(
                        username,
                        peerListener.getPort()
                );

                SwingUtilities.invokeLater(() -> {
                    if (response.startsWith("SUCCESS")) {
                        new ChatFrame(
                                username,
                                chatClient,
                                peerListener
                        ).setVisible(true);

                        dispose();
                    } else {
                        statusLabel.setText(response);
                        peerListener.stopListener();
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        statusLabel.setText("Khong ket noi duoc Server")
                );
            }
        }).start();
    }
}