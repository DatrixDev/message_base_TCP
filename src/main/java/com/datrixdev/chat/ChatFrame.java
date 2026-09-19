package com.datrixdev.chat;

import javax.swing.*;

public class ChatFrame extends JFrame {
    private String username;
    private ChatClient chatClient;
    private DefaultListModel<String> userListModel;

    public ChatFrame(String username,ChatClient chatClient){
        this.username = username;
        this.chatClient = chatClient;

        setTitle("P2P Chat - " + username);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);

        JButton refreshButton = new JButton("Tải lại danh sách");
        refreshButton.addActionListener(e ->());

        
    }

}
