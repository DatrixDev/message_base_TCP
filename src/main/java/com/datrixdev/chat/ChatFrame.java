package com.datrixdev.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatFrame extends JFrame implements PeerEventListener {
    private String username;
    private ChatClient chatClient;
    private PeerListener peerListener;

    private DefaultListModel<String> userListModel;
    private JList<String> userList;
    private JTextArea chatArea;
    private JTextField messageField;
    private Map<String, StringBuilder> conversations =
            new ConcurrentHashMap<>();

    public ChatFrame(
            String username,
            ChatClient chatClient,
            PeerListener peerListener
    ) {
        this.username = username;
        this.chatClient = chatClient;
        this.peerListener = peerListener;

        peerListener.setEventListener(this);
        chatClient.setOnlineUsersListener(this::updateOnlineUsers);

        setTitle("P2P Chat - " + username);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);

        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedConversation();
            }
        });


        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(180, 0));
        leftPanel.setBorder(
                BorderFactory.createTitledBorder("Online")
        );

        leftPanel.add(new JScrollPane(userList), BorderLayout.CENTER);


        chatArea = new JTextArea();
        chatArea.setEditable(false);

        messageField = new JTextField();
        messageField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("Gửi");
        sendButton.addActionListener(e -> sendMessage());

        JButton fileButton = new JButton("Gửi file");
        fileButton.addActionListener(e -> chooseAndSendFile());

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.add(fileButton, BorderLayout.WEST);
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(leftPanel, BorderLayout.WEST);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    chatClient.disconnect();
                } catch (Exception ex) {
                    System.out.println("Khong the ngat ket noi Server");
                }

                peerListener.stopListener();
            }
        });

        loadOnlineUsers();
    }

    private void loadOnlineUsers() {
        chatClient.requestOnlineUsers();
    }

    private void updateOnlineUsers(String response) {
        SwingUtilities.invokeLater(() -> {
            String selectedUser = userList.getSelectedValue();

            userListModel.clear();

            String users = response.substring(6);

            if (!users.isEmpty()) {
                for (String user : users.split(",")) {
                    if (!user.equals(username)) {
                        userListModel.addElement(user);
                    }
                }
            }

            if (selectedUser != null) {
                userList.setSelectedValue(selectedUser, true);
            }
        });
    }

    private void sendMessage() {
        String targetUser = userList.getSelectedValue();
        String message = messageField.getText().trim();

        if (targetUser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hay chon nguoi can nhan"
            );
            return;
        }

        if (message.isEmpty()) {
            return;
        }

        new Thread(() -> {
            try {
                String response = chatClient.findUser(targetUser);

                if (response.startsWith("PEER|")) {
                    String[] parts = response.split("\\|");
                    String[] address = parts[2].split(":");

                    String ip = address[0];
                    int port = Integer.parseInt(address[1]);

                    PeerSender.sendMessage(
                            ip,
                            port,
                            username,
                            message
                    );

                    addToConversation(
                            targetUser,
                            "Bạn: " + message
                    );

                    SwingUtilities.invokeLater(() ->
                            messageField.setText("")
                    );

                } else {
                    addToConversation(
                            targetUser,
                            "Người dùng không online"
                    );
                }

            } catch (Exception e) {
                addToConversation(
                        targetUser,
                        "Khong gui duoc tin nhan"
                );
            }
        }).start();
    }

    private void chooseAndSendFile() {
        String targetUser = userList.getSelectedValue();

        if (targetUser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hay chon nguoi can nhan file"
            );
            return;
        }

        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();

        new Thread(() -> {
            try {
                String response = chatClient.findUser(targetUser);

                if (response.startsWith("PEER|")) {
                    String[] parts = response.split("\\|");
                    String[] address = parts[2].split(":");

                    String ip = address[0];
                    int port = Integer.parseInt(address[1]);

                    PeerSender.sendFile(
                            ip,
                            port,
                            username,
                            selectedFile
                    );

                    addToConversation(
                            targetUser,
                            "Ban da gui file: "
                                    + selectedFile.getName()
                    );

                } else {
                    addToConversation(
                            targetUser,
                            "Nguoi dung khong online"
                    );
                }

            } catch (Exception e) {
                addToConversation(
                        targetUser,
                        "Khong gui duoc file"
                );
            }
        }).start();
    }

    @Override
    public void onMessageReceived(String from, String message) {
        addToConversation(from, from + ": " + message);
    }

    @Override
    public void onFileReceived(
            String from,
            String fileName,
            File savedFile
    ) {
        addToConversation(
                from,
                from + " đã gửi file: " + fileName
                        + "\nĐã lưu tại: "
                        + savedFile.getAbsolutePath()
        );
    }

    private void addToConversation(String user, String text) {
        conversations
                .computeIfAbsent(user, key -> new StringBuilder())
                .append(text)
                .append("\n");

        SwingUtilities.invokeLater(() -> {
            String selectedUser = userList.getSelectedValue();

            if (user.equals(selectedUser)) {
                showSelectedConversation();
            }
        });
    }

    private void showSelectedConversation() {
        String selectedUser = userList.getSelectedValue();

        if (selectedUser == null) {
            chatArea.setText("");
            return;
        }

        StringBuilder conversation = conversations.get(selectedUser);

        if (conversation == null) {
            chatArea.setText("");
        } else {
            chatArea.setText(conversation.toString());
        }
    }
}