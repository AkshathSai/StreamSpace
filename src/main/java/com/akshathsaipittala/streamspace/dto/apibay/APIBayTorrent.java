package com.akshathsaipittala.streamspace.dto.apibay;

public record APIBayTorrent(String id,
                     String name,
                     String info_hash,
                     String leechers,
                     String seeders,
                     String num_files,
                     String size,
                     String username,
                     String added,
                     String status,
                     String category,
                     String imdb) {
}