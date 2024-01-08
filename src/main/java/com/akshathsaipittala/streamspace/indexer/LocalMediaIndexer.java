package com.akshathsaipittala.streamspace.indexer;

import com.akshathsaipittala.streamspace.entity.Movie;
import com.akshathsaipittala.streamspace.entity.Music;
import com.akshathsaipittala.streamspace.repository.MovieRepository;
import com.akshathsaipittala.streamspace.repository.MusicRepository;
import com.akshathsaipittala.streamspace.utils.ApplicationConstants;
import com.akshathsaipittala.streamspace.utils.RuntimeHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalMediaIndexer {

    final MediaLibrary mediaLibrary;
    final RuntimeHelper runtimeHelper;
    final MovieRepository movieRepository;
    final MusicRepository musicRepository;

    public MediaLibrary indexMedia() throws IOException {

        List<Path> paths = findLocalMediaFiles(runtimeHelper.MOVIES_FOLDER, runtimeHelper.MUSIC_FOLDER);

        List<Path> musicPaths = paths.stream()
                .filter(path -> path.toString().endsWith(".mp3"))
                .toList();

        List<Path> moviePaths = paths.stream()
                .filter(path -> path.toString().endsWith(".mp4") || path.toString().endsWith(".mkv") || path.toString().endsWith(".avi") || path.toString().endsWith(".mpeg"))
                .toList();

        List<Movie> movies = createMovieEntities(moviePaths);
        List<Music> music = createMusicEntities(musicPaths);
        mediaLibrary.setMovies(movies);
        mediaLibrary.setMusic(music);

        movieRepository.saveAll(movies);
        musicRepository.saveAll(music);
        return mediaLibrary;
    }

    public List<Path> findLocalMediaFiles(String... locations) throws IOException {
        String pattern = "glob:**/*.{mp4,mpeg,mp3,mkv}";

        List<Path> matchingPaths = new ArrayList<>();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pattern);

        for (String location : locations) {
            Path start = Paths.get(location);

            Files.walkFileTree(start, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                    if (matcher.matches(path)) {
                        matchingPaths.add(path);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        return matchingPaths;
    }

    private List<Movie> createMovieEntities(List<Path> paths) throws IOException {

        List<Movie> moviesList = new ArrayList<>();

        for (Path entry : paths) {

            Movie movie = new Movie();

            File file = new File(entry.toString());
            log.info(entry.toString());

            // Get the immediate parent folder name
            String parentFolderName = entry.getParent().getFileName().toString();

            String contentStoreDir;
            if (parentFolderName.equals("Movies")) {
                // If the parent folder is also /Music, use the SPRING_CONTENT_MUSIC_STORE directly
                //contentStoreDir = RuntimeHelper.SPRING_CONTENT_MOVIES_STORE + file.getName();
                contentStoreDir = runtimeHelper.SPRING_CONTENT_MOVIES_STORE + URLDecoder.decode(file.getName(), StandardCharsets.UTF_8);

            } else {
                // Construct storeDir by appending parentFolderName and file.getName()
                //contentStoreDir = RuntimeHelper.SPRING_CONTENT_MOVIES_STORE + parentFolderName + "/" + file.getName();
                contentStoreDir = runtimeHelper.SPRING_CONTENT_MOVIES_STORE + parentFolderName + "/" + URLDecoder.decode(file.getName(), StandardCharsets.UTF_8);
            }

            String userDir = runtimeHelper.USER_HOME + File.separator + contentStoreDir;
            log.info("Content Store {}", contentStoreDir);
            log.info("Local Directory {}", userDir);

            movie.setName(entry.getFileName().toString());
            movie.setContentLength(Files.size(entry));
            movie.setSummary(entry.getFileName().toString());
            movie.setContentId(contentStoreDir);
            String contentType = MediaTypeFactory.getMediaType(new FileSystemResource(entry)).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
            movie.setContentMimeType(contentType);
            //movie.setMovieCode(entry.getFileName().toString());
            movie.setMovieCode(URLDecoder.decode(entry.getFileName().toString(), StandardCharsets.UTF_8));
            movie.setMediaSource(ApplicationConstants.LOCAL_MEDIA);
            moviesList.add(movie);
        }

        return moviesList;
    }

    private List<Music> createMusicEntities(List<Path> paths) throws IOException {

        List<Music> musicList = new ArrayList<>();

        for (Path entry : paths) {
            Music music = new Music();
            File file = new File(entry.toString());
            log.info(entry.toString());

            // Get the immediate parent folder name
            String parentFolderName = entry.getParent().getFileName().toString();

            String contentStoreDir;
            if (parentFolderName.equals("Music")) {
                // If the parent folder is also /Music, use the SPRING_CONTENT_MUSIC_STORE directly
                contentStoreDir = runtimeHelper.SPRING_CONTENT_MUSIC_STORE + file.getName();
            } else {
                // Construct storeDir by appending parentFolderName and file.getName()
                contentStoreDir = runtimeHelper.SPRING_CONTENT_MUSIC_STORE + parentFolderName + "/" + file.getName();
            }

            String userDir = runtimeHelper.USER_HOME + File.separator + contentStoreDir;
            log.info("Content Store {}", contentStoreDir);
            log.info("Local Directory {}", userDir);

            music.setName(entry.getFileName().toString());
            music.setContentLength(Files.size(entry));
            music.setSummary(entry.getFileName().toString());
            music.setContentId(contentStoreDir);
            String contentType = MediaTypeFactory.getMediaType(new FileSystemResource(entry)).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
            music.setContentMimeType(contentType);
            music.setMediaSource(ApplicationConstants.LOCAL_MEDIA);
            music.setMusicId(entry.getFileName().toString());
            musicList.add(music);
        }

        return musicList;
    }

}
