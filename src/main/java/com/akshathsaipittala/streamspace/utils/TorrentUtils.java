package com.akshathsaipittala.streamspace.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.DecimalFormat;
import java.util.UUID;

@Component
public class TorrentUtils {

    public static String convertSize(long sizeInBytes) {
        if (sizeInBytes <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(sizeInBytes) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(sizeInBytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getMagnetUri(String torrentHash) {
        return "magnet:?xt=urn:btih:" + torrentHash;
    }

    public static int getRandomFreePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }

    // Define a method to generate a unique movieCode (e.g., using UUID)
    public static String generateUniqueCode() {
        return UUID.randomUUID().toString();
    }

    private TorrentUtils() {}

}
