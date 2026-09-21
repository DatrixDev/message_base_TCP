package com.datrixdev.chat;

public class ChatMessage {

    private String text;
    private boolean mine;

    public ChatMessage(String text, boolean mine) {
        this.text = text;
        this.mine = mine;
    }

    public String getText() {
        return text;
    }

    public boolean isMine() {
        return mine;
    }
}