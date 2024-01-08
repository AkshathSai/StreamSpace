package com.akshathsaipittala.streamspace.dto.yts;

import java.util.List;

public record YTSMovieDetails(
        String id,
        String url,
        String imdb_code,
        String title,
        String title_english,
        String title_long,
        String year,
        String rating,
        String runtime,
        String[] genres,
        String language,
        Object background_image,
        Object background_image_original,
        Object small_cover_image,
        Object medium_cover_image,
        Object large_cover_image,
        List<YTSTorrent> torrents) {
}

