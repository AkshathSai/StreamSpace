package com.akshathsaipittala.streamspace.web.api;

import com.akshathsaipittala.streamspace.dto.yts.YTSMovieRecord;
import com.akshathsaipittala.streamspace.dto.yts.YTSMoviesRecord;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

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

    @GetExchange("movie_details.json?movie_id={id}")
    YTSMovieRecord getMovieDetails(@PathVariable int id);

    @GetExchange("movie_suggestions.json?movie_id={id}")
    YTSMoviesRecord getSuggestedMovies(@PathVariable int id);

    @GetExchange("list_movies.json?query_term={term}")
    YTSMoviesRecord ytsSearchV2(@PathVariable String term);

}
