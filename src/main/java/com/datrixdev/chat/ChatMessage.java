package com.datrixdev.chat;

import java.io.File;

public class ChatMessage {
    private final String text;
    private final boolean mine;
    private final File file;

    public ChatMessage(String text, boolean mine) {
        this(text, mine, null);
    }

    public ChatMessage(String text, boolean mine, File file) {
        this.text = text;
        this.mine = mine;
        this.file = file;
    }

    public String getText() {
        return text;
    }

    public boolean isMine() {
        return mine;
    }

    public File getFile() {
        return file;
    }
}