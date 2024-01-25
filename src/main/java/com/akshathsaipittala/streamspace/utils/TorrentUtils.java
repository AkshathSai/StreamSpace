package com.akshathsaipittala.streamspace.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class TorrentUtils {

    public static String createMagnetUri(String torrentHash) {
        return "magnet:?xt=urn:btih:" + torrentHash;
    }

    public static int getRandomFreePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }

    private TorrentUtils() {}

}
