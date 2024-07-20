package com.akshathsaipittala.streamspace.www;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange(url = "https://yts.mx/api/v2/", accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface YTSAPIClient {

    @GetExchange("list_movies.json?limit=6&sort_by=download_count")
    YTSMoviesRecord getMostWatchedMovies();

    @GetExchange("list_movies.json?limit=50&sort_by=download_count&page={page}")
    YTSMoviesRecord getMostWatchedMovies(@PathVariable int page);

    @GetExchange("list_movies.json?sort=date_added&order_by=desc&limit=6")
    YTSMoviesRecord getLatestMovies();

    @GetExchange("list_movies.json?sort=date_added&order_by=desc&limit=50&page={page}")
    YTSMoviesRecord getLatestMovies(@PathVariable int page);

    @GetExchange("list_movies.json?genre=comedy&sort=date_added&limit=6")
    YTSMoviesRecord getLatestComedyMovies();

    @GetExchange("list_movies.json?genre=comedy&sort=date_added&limit=50&page={page}")
    YTSMoviesRecord getLatestComedyMovies(@PathVariable int page);

    @GetExchange("list_movies.json?genre=comedy&sort_by=download_count&limit=6")
    YTSMoviesRecord getMustWatch();

    @GetExchange("list_movies.json?genre=comedy&sort_by=download_count&limit=50&page={page}")
    YTSMoviesRecord getMustWatch(@PathVariable int page);

    @GetExchange("list_movies.json?minimum_rating=7&limit=6")
    YTSMoviesRecord getIMDBHighestRated();

    @GetExchange("list_movies.json?minimum_rating=7&limit=50&page={page}")
    YTSMoviesRecord getIMDBHighestRated(@PathVariable int page);

    @GetExchange("list_movies.json?sort_by=like_count&order_by=desc&limit=6")
    YTSMoviesRecord getMostLiked();

    @GetExchange("list_movies.json?sort_by=like_count&order_by=desc&limit=50&page={page}")
    YTSMoviesRecord getMostLiked(@PathVariable int page);

    /**
     * TODO: Enhance API to fetch some more info
     * https://yts.mx/api/v2/movie_details.json?movie_id=36846&with_images=true&with_cast=true
     */
    @GetExchange("movie_details.json?movie_id={id}")
    YTSMovieRecord getMovieDetails(@PathVariable int id);

    @GetExchange("movie_suggestions.json?movie_id={id}")
    YTSMoviesRecord getSuggestedMovies(@PathVariable int id);

    @GetExchange("list_movies.json?query_term={term}")
    YTSMoviesRecord ytsSearchV2(@PathVariable String term);

    record YTSMovieRecord(String status, String status_message, YTSMovieData data) {
    }

    record YTSMovieData(YTSMovieDetails movie) {
    }

    record YTSMoviesRecord(String status, String status_message, YTSData data) {
    }

    record YTSData(List<YTSMovieDetails> movies) {
    }

    record YTSMovieDetails(
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
}

@Slf4j
@Controller
@RequestMapping("/yts")
@RequiredArgsConstructor
class YTSMoviesController {

    final YTSAPIClient ytsapiClient;

    @HxRequest
    @GetMapping("/movies/{id}")
    HtmxResponse getNewOverview(@PathVariable("id") int id, Model model) {

        model.addAttribute("ytsMovieRecord", ytsapiClient.getMovieDetails(id));
        model.addAttribute("ytsSuggestedRecord", ytsapiClient.getSuggestedMovies(id));

        return HtmxResponse.builder()
                .view("ytsMovie :: ytsMovieOverview")
                .build();
    }

    @GetMapping("/movies/{id}")
    String getYTSMovieOverview(@PathVariable("id") int id, Model model) {

        model.addAttribute("ytsMovieRecord", ytsapiClient.getMovieDetails(id));
        model.addAttribute("ytsSuggestedRecord", ytsapiClient.getSuggestedMovies(id));

        return "ytsMovie";
    }

    @GetMapping("/movies/cat/{category}")
    HtmxResponse viewAllPage(Model model,
                                    @RequestParam(defaultValue = "1") int page,
                                    @PathVariable("category") String category) {

        model.addAttribute("category", category);

        if (category.equals("latest")) {
            model.addAttribute("ytsMoviesRecord", ytsapiClient.getLatestMovies(page));
        } else if (category.equals("mostliked")) {
            model.addAttribute("ytsMoviesRecord", ytsapiClient.getMostLiked(page));
        } else if (category.equals("imdbrating")) {
            model.addAttribute("ytsMoviesRecord", ytsapiClient.getIMDBHighestRated(page));
        } else if (category.equals("mostwatched")) {
            model.addAttribute("ytsMoviesRecord", ytsapiClient.getMostWatchedMovies(page));
        } else if (category.equals("latestcomedies")) {
            model.addAttribute("ytsMoviesRecord", ytsapiClient.getLatestComedyMovies(page));
        } else if (category.equals("mustwatch")) {
            model.addAttribute("ytsMoviesRecord", ytsapiClient.getMustWatch(page));
        }

        model.addAttribute("currentPage", page);

        return HtmxResponse.builder()
                .view("viewAll :: gallery")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsMostWatched")
    HtmxResponse ytsMostWatched(Model model) {
        model.addAttribute("ytsMostWatchedRecord", ytsapiClient.getMostWatchedMovies());

        return HtmxResponse.builder()
                .view("movies :: ytsMostWatched")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsLatest")
    HtmxResponse ytsLatest(Model model) {
        model.addAttribute("ytsLatestRecord", ytsapiClient.getLatestMovies());

        return HtmxResponse.builder()
                .view("movies :: ytsLatest")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsMostLiked")
    HtmxResponse ytsMostLiked(Model model) {
        model.addAttribute("ytsMostLikedRecord", ytsapiClient.getMostLiked());

        return HtmxResponse.builder()
                .view("movies :: ytsMostLiked")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsImdbRating")
    HtmxResponse ytsImdbRating(Model model) {
        model.addAttribute("ytsIMDBHighestRatedRecord", ytsapiClient.getIMDBHighestRated());

        return HtmxResponse.builder()
                .view("movies :: ytsImdbRating")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsLatestComedies")
    HtmxResponse ytsLatestComedies(Model model) {
        model.addAttribute("ytsLatestComedyRecord", ytsapiClient.getLatestComedyMovies());

        return HtmxResponse.builder()
                .view("movies :: ytsLatestComedies")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsMustWatch")
    HtmxResponse ytsMustWatch(Model model) {

        model.addAttribute("ytsMustWatchRecord", ytsapiClient.getMustWatch());

        return HtmxResponse.builder()
                .view("movies :: ytsMustWatch")
                .build();
    }

}