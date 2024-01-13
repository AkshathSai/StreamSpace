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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Async
@Slf4j
@Service
@RequiredArgsConstructor
public class LocalMediaIndexer {

    final MediaLibrary mediaLibrary;
    final RuntimeHelper runtimeHelper;
    final MovieRepository movieRepository;
    final MusicRepository musicRepository;

    public void indexMedia() throws IOException, ExecutionException, InterruptedException {

        CompletableFuture<List<Path>> paths = findLocalMediaFiles(runtimeHelper.MOVIES_FOLDER, runtimeHelper.MUSIC_FOLDER);

        List<Path> musicPaths = paths.get().stream()
                .filter(path -> path.toString().endsWith(".mp3") || path.toString().endsWith(".flac"))
                .toList();

        List<Path> moviePaths = paths.get().stream()
                .filter(path -> path.toString().endsWith(".mp4") || path.toString().endsWith(".mkv") || path.toString().endsWith(".avi") || path.toString().endsWith(".mpeg"))
                .toList();

        List<Movie> movies = createMovieEntities(moviePaths);
        List<Music> music = createMusicEntities(musicPaths);
        mediaLibrary.setMovies(movies);
        mediaLibrary.setMusic(music);

        movieRepository.saveAll(movies);
        musicRepository.saveAll(music);
    }

    public CompletableFuture<List<Path>> findLocalMediaFiles(String... locations) throws IOException {
        String pattern = "glob:**/*.{mp4,mpeg,mp3,mkv,flac}";

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

        return CompletableFuture.completedFuture(matchingPaths);
    }

    private List<Movie> createMovieEntities(List<Path> paths) throws IOException {

        List<Movie> moviesList = new ArrayList<>();

        for (Path entry : paths) {

            Movie movie = new Movie();

            File file = new File(entry.toString());
            log.debug(entry.toString());

            // Get the immediate parent folder name
            String parentFolderName = entry.getParent().getFileName().toString();

            String contentStoreDir;
            if (parentFolderName.equals("Movies")) {
                // If the parent folder is also /Music, use the SPRING_CONTENT_MUSIC_STORE directly
                contentStoreDir = runtimeHelper.SPRING_CONTENT_MOVIES_STORE + URLDecoder.decode(file.getName(), StandardCharsets.UTF_8);

            } else {
                // Construct storeDir by appending parentFolderName and file.getName()
                contentStoreDir = runtimeHelper.SPRING_CONTENT_MOVIES_STORE + parentFolderName + "/" + URLDecoder.decode(file.getName(), StandardCharsets.UTF_8);
            }

            String userDir = runtimeHelper.USER_HOME + File.separator + contentStoreDir;
            log.debug("Content Store {}", contentStoreDir);
            log.debug("Local Directory {}", userDir);

            movie.setName(entry.getFileName().toString());
            movie.setContentLength(Files.size(entry));
            movie.setSummary(entry.getFileName().toString());
            movie.setContentId(URLDecoder.decode(contentStoreDir, StandardCharsets.UTF_8));
            String contentType = MediaTypeFactory.getMediaType(new FileSystemResource(entry)).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
            movie.setContentMimeType(contentType);
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
            log.debug(entry.toString());

            // Get the immediate parent folder name
            String parentFolderName = entry.getParent().getFileName().toString();

            String encodedFileName = encodePathSegment(entry.getFileName().toString());
            String contentStoreDir;
            if (parentFolderName.equals("Music")) {
                // If the parent folder is also /Music, use the SPRING_CONTENT_MUSIC_STORE directly
                //contentStoreDir = runtimeHelper.SPRING_CONTENT_MUSIC_STORE + URLDecoder.decode(file.getName(), StandardCharsets.UTF_8);
                contentStoreDir = runtimeHelper.SPRING_CONTENT_MUSIC_STORE + encodedFileName;
            } else {
                // Construct storeDir by appending parentFolderName and file.getName()
                //contentStoreDir = runtimeHelper.SPRING_CONTENT_MUSIC_STORE + parentFolderName + "/" + URLDecoder.decode(file.getName(), StandardCharsets.UTF_8);
                contentStoreDir = runtimeHelper.SPRING_CONTENT_MUSIC_STORE + parentFolderName + "/" + encodedFileName;
            }

            String userDir = runtimeHelper.USER_HOME + File.separator + contentStoreDir;
            log.debug("Content Store {}", contentStoreDir);
            log.debug("Local Directory {}", userDir);

            music.setName(URLDecoder.decode(entry.getFileName().toString(), StandardCharsets.UTF_8));
            music.setContentLength(Files.size(entry));
            music.setSummary(entry.getFileName().toString());
            music.setContentId(URLDecoder.decode(contentStoreDir, StandardCharsets.UTF_8));
            String contentType = MediaTypeFactory.getMediaType(new FileSystemResource(entry)).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
            music.setContentMimeType(contentType);
            music.setMediaSource(ApplicationConstants.LOCAL_MEDIA);
            music.setMusicId(URLDecoder.decode(entry.getFileName().toString(), StandardCharsets.UTF_8));
            musicList.add(music);
        }

        return musicList;
    }

    private String encodePathSegment(String pathSegment) {
        return UriUtils.encodePathSegment(pathSegment, StandardCharsets.UTF_8.name());
    }

}
