package com.akshathsaipittala.streamspace.indexer;

import com.akshathsaipittala.streamspace.entity.Movie;
import com.akshathsaipittala.streamspace.entity.Music;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class MediaLibrary {

    private List<Movie> movies;
    private List<Music> music;

    public MediaLibrary(List<Movie> movies, List<Music> music) {
        this.movies = movies;
        this.music = music;
    }
}
