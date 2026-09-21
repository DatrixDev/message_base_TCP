package com.datrixdev.chat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UserListRenderer extends JPanel implements ListCellRenderer<String> {
    private final JLabel avatarLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("● Đang hoạt động");
    private final Map<String, ImageIcon> avatarCache = new HashMap<>();

    public UserListRenderer() {
        setLayout(new BorderLayout(12, 0));
        setBorder(new EmptyBorder(8, 12, 8, 12));
        avatarLabel.setPreferredSize(new Dimension(45, 45));

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(Color.BLACK);

        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(50, 180, 90));

        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createVerticalGlue());

        add(avatarLabel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends String> list,
            String value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        nameLabel.setText(value);

        ImageIcon avatar = avatarCache.get(value);

        if (avatar == null) {
            try {
                String seed = URLEncoder.encode(value, StandardCharsets.UTF_8);
                URL url = new URL(
                        "https://api.dicebear.com/9.x/avataaars/png?seed=" + seed + "&size=256"
                );

                ImageIcon icon = new ImageIcon(url);
                avatar = makeCircular(icon.getImage(), 45);
                avatarCache.put(value, avatar);
            } catch (Exception e) {
                avatar = null;
            }
        }

        avatarLabel.setIcon(avatar);

        if (isSelected) {
            setBackground(new Color(235, 235, 235));
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }

    private ImageIcon makeCircular(Image image, int size) {
        BufferedImage circle = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circle.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setClip(new Ellipse2D.Double(0, 0, size, size));
        g2.drawImage(image, 0, 0, size, size, null);
        g2.dispose();

        return new ImageIcon(circle);
    }
}