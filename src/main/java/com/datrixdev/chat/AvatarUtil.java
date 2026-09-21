package com.datrixdev.chat;

import javax.swing.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AvatarUtil {
    private static final Map<String, ImageIcon> cache = new HashMap<>();

    public static ImageIcon getAvatar(String username, int size) {
        String key = username + size;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        try {
            String seed = URLEncoder.encode(username, StandardCharsets.UTF_8);
            URL url = new URL(
                    "https://api.dicebear.com/9.x/avataaars/png?seed=" +
                            seed + "&size=" + size + "&radius=50"
            );

            ImageIcon icon = new ImageIcon(url);
            cache.put(key, icon);
            return icon;
        } catch (Exception e) {
            return null;
        }
    }
}