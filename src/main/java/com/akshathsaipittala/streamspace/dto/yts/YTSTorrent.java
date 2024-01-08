package com.akshathsaipittala.streamspace.dto.yts;

record YTSTorrent(
        String url,
        String hash,
        String quality,
        String type,
        String is_repack,
        String video_codec,
        String bit_depth,
        String audio_channels,
        int seeds,
        int peers,
        String size,
        long size_bytes,
        String date_uploaded) {
}
