package com.datrixdev.chat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MessageBubble extends JPanel {
    public MessageBubble(String message, boolean mine, String username) {
        setOpaque(false);
        setLayout(new BorderLayout());

        JTextArea text = new JTextArea(message);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setFont(new Font("Arial", Font.PLAIN, 15));
        text.setForeground(Color.BLACK);
        text.setBackground(mine ? new Color(220, 235, 255) : new Color(240, 240, 240));
        text.setBorder(new EmptyBorder(10, 14, 10, 14));
        text.setColumns(25);

        JLabel avatar = new JLabel();
        avatar.setIcon(AvatarUtil.getAvatar(username, 36));
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel content = new JPanel(new FlowLayout(
                mine ? FlowLayout.RIGHT : FlowLayout.LEFT,
                8,
                0
        ));
        content.setOpaque(false);

        if (mine) {
            content.add(text);
            content.add(avatar);
        } else {
            content.add(avatar);
            content.add(text);
        }

        add(content, BorderLayout.CENTER);
        setBorder(new EmptyBorder(5, 10, 5, 10));
    }
}