package com.akshathsaipittala.streamspace.dto.piratebay;

public record Torrent (
        String title,
        String uploadedOn,
        String size,
        String hyperLink,
        String seeders,
        String leechers,
        String magnetLink,
        String user) {
}
