package com.datrixdev.chat;

import java.io.File;

public interface PeerEventListener {
    void onMessageReceived(String form,String message);
    void onFileReceived(String form, String fileName, File savedFile);
}
