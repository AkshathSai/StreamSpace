package com.akshathsaipittala.streamspace.indexer;

import com.akshathsaipittala.streamspace.entity.Movie;
import com.akshathsaipittala.streamspace.entity.Song;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class MediaLibrary {

    private List<Movie> movies;
    private List<Song> songs;

    public MediaLibrary(List<Movie> movies, List<Song> songs) {
        this.movies = movies;
        this.songs = songs;
    }
}
