package com.datrixdev.chat;

import java.io.File;

public interface PeerEventListener {
    void onMessageReceived(String from, String message);

    void onFileReceived(String from, String fileName, File savedFile);
}