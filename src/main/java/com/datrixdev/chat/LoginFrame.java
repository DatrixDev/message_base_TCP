package com.datrixdev.chat;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("P2P BY VO QUOC DAT - NGUYEN KY VY");
        setSize(350,220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4,1,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,25,20,25));

        JLabel titleLabel  = new JLabel("P2P Chat & File",SwingConstants.CENTER);
        usernameField = new JTextField();

        JButton connectButton = new JButton("KET NOI");
        statusLabel = new JLabel("Nhập tên tài khoản",SwingConstants.CENTER);

        panel.add(titleLabel);
        panel.add(usernameField);
        panel.add(connectButton);
        panel.add(statusLabel);

        add(panel);

        connectButton.addActionListener(e ->connectToServer());
    }

    public void connectToServer() {
        String userName = usernameField.getText().trim();

        if(userName.isEmpty()){
            statusLabel.setText("Vui lòng nhập tên");
            return;
        }
        new Thread(() -> {
            try{
                ChatClient chatClient = new ChatClient("127.0.0.1");
                String response = chatClient.register(userName,5001);

                SwingUtilities.invokeLater(() ->

                        {
                            if(response.startsWith("SUCCESS")) {
                                new ChatFrame(userName,chatClient).setVisible (true);
                                dispose();
                            }
                            else {
                                statusLabel.setText(response);
                            }
                        });

            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                        statusLabel.setText("Khong ket noi duoc Server")
                );
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
