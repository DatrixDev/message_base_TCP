package com.datrixdev.chat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatFrame extends JFrame implements PeerEventListener {
    private final String username;
    private final ChatClient chatClient;
    private final PeerListener peerListener;
    private DefaultListModel<String> userListModel;
    private JList<String> userList;
    private JPanel messagePanel;
    private JScrollPane messageScrollPane;
    private JTextField messageField;
    private final Map<String, List<ChatMessage>> conversations = new ConcurrentHashMap<>();

    public ChatFrame(String username, ChatClient chatClient, PeerListener peerListener) {
        this.username = username;
        this.chatClient = chatClient;
        this.peerListener = peerListener;

        peerListener.setEventListener(this);
        chatClient.setOnlineUsersListener(this::updateOnlineUsers);

        setTitle("P2P Chat");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(35, 49, 63));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 0, 1,
                        new Color(220, 220, 220)
                )
        );
        JLabel titleLabel = new JLabel("  P2P Chat");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0,
                                new Color(230, 230, 230)
                        ),
                        new EmptyBorder(10, 15, 10, 15)
                )
        );
        leftPanel.add(titleLabel, BorderLayout.NORTH);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setBackground(Color.WHITE);
        userList.setForeground(Color.BLACK);
        userList.setSelectionBackground(new Color(235, 235, 235));
        userList.setSelectionForeground(Color.BLACK);
        userList.setFixedCellHeight(75);
        userList.setBorder(new EmptyBorder(10, 10, 10, 10));
        userList.setCellRenderer(new UserListRenderer());

        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setBorder(null);
        userScroll.getViewport().setBackground(Color.WHITE);
        leftPanel.add(userScroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBackground(Color.WHITE);
        chatHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel chatAvatar = new JLabel();
        chatAvatar.setPreferredSize(new Dimension(42, 42));

        JLabel chatTitle = new JLabel("Chọn người để trò chuyện");
        chatTitle.setForeground(Color.BLACK);
        chatTitle.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel userHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userHeader.setOpaque(false);
        userHeader.add(chatAvatar);
        userHeader.add(chatTitle);

        chatHeader.add(userHeader, BorderLayout.WEST);
        centerPanel.add(chatHeader, BorderLayout.NORTH);
        centerPanel.add(chatHeader, BorderLayout.NORTH);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(new Color(245, 245, 245));
        messagePanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        messageScrollPane = new JScrollPane(messagePanel);
        messageScrollPane.setBorder(null);
        messageScrollPane.getViewport().setBackground(new Color(245, 245, 245));
        messageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(messageScrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(65, 78, 92));
        inputPanel.setBorder(new EmptyBorder(3, 5, 3, 5));

        JButton fileButton = new JButton("📎");
        fileButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        fileButton.setForeground(new Color(210, 215, 220));
        fileButton.setBackground(new Color(65, 78, 92));
        fileButton.setBorderPainted(false);
        fileButton.setFocusPainted(false);
        fileButton.setContentAreaFilled(false);
        fileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fileButton.addActionListener(e -> chooseAndSendFile());

        messageField = new JTextField();
        messageField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        messageField.setForeground(Color.WHITE);
        messageField.setCaretColor(Color.WHITE);
        messageField.setBackground(new Color(65, 78, 92));
        messageField.setBorder(new EmptyBorder(10, 8, 10, 8));
        messageField.addActionListener(e -> sendMessage());
        messageField.setText("Write a message here...");
        messageField.setForeground(new Color(170, 175, 180));

        messageField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (messageField.getText().equals("Write a message here...")) {
                    messageField.setText("");
                    messageField.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (messageField.getText().isEmpty()) {
                    messageField.setText("Write a message here...");
                    messageField.setForeground(new Color(170, 175, 180));
                }
            }
        });
        JButton sendButton = new JButton("➤");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        sendButton.setForeground(new Color(0, 170, 255));
        sendButton.setBackground(new Color(65, 78, 92));
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(fileButton, BorderLayout.WEST);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedUser = userList.getSelectedValue();
                if (selectedUser != null) {
                    chatTitle.setText(selectedUser);
                    chatAvatar.setIcon(AvatarUtil.getAvatar(selectedUser, 42));
                    showSelectedConversation();
                }
            }
        });

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
            JOptionPane.showMessageDialog(this, "Hãy chọn người cần nhắn");
            return;
        }

        if (message.isEmpty() || message.equals("Write a message here...")) {
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

                    PeerSender.sendMessage(ip, port, username, message);
                    addMessage(targetUser, message, true);
                    SwingUtilities.invokeLater(() -> messageField.setText(""));
                } else {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Người dùng không online")
                    );
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Không gửi được tin nhắn")
                );
            }
        }).start();
    }

    private void chooseAndSendFile() {
        String targetUser = userList.getSelectedValue();

        if (targetUser == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn người cần nhận file");
            return;
        }

        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
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

                    PeerSender.sendFile(ip, port, username, selectedFile);
                    addMessage(targetUser, "Đã gửi file: " + selectedFile.getName(), true);
                } else {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Người dùng không online")
                    );
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Không gửi được file")
                );
            }
        }).start();
    }

    @Override
    public void onMessageReceived(String from, String message) {
        addMessage(from, message, false);
    }

    @Override
    public void onFileReceived(String from, String fileName, File savedFile) {
        String text = "Đã nhận file: " + fileName + "\nLưu tại: " + savedFile.getAbsolutePath();
        addMessage(from, text, false);
    }

    private void addMessage(String user, String text, boolean mine) {
        conversations.computeIfAbsent(user, key -> new ArrayList<>()).add(new ChatMessage(text, mine));

        SwingUtilities.invokeLater(() -> {
            String selectedUser = userList.getSelectedValue();
            if (user.equals(selectedUser)) {
                showSelectedConversation();
            }
        });
    }

    private void showSelectedConversation() {
        String selectedUser = userList.getSelectedValue();
        messagePanel.removeAll();

        if (selectedUser == null) {
            messagePanel.revalidate();
            messagePanel.repaint();
            return;
        }

        List<ChatMessage> messages = conversations.get(selectedUser);

        if (messages != null) {
            for (ChatMessage message : messages) {
                MessageBubble bubble = new MessageBubble(
                        message.getText(),
                        message.isMine(),
                        message.isMine() ? username : selectedUser
                );
                messagePanel.add(bubble);
                messagePanel.add(Box.createVerticalStrut(5));
            }
        }

        messagePanel.revalidate();
        messagePanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = messageScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}