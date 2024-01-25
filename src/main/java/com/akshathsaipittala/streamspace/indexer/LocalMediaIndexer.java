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
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Async
@Slf4j
@Service
@RequiredArgsConstructor
public class LocalMediaIndexer {

    final MediaLibrary mediaLibrary;
    final RuntimeHelper runtimeHelper;
    final MovieRepository movieRepository;
    final MusicRepository musicRepository;

    /**
     * Concurrent indexer
     */
    public void indexMedia() throws IOException {

        findLocalMediaFiles(
                runtimeHelper.getMoviesFolderPath(),
                runtimeHelper.getMusicFolderPath()
        )
                .thenApply(paths -> {
                    List<Path> musicPaths = paths.parallelStream()
                            .filter(path -> path.toString().endsWith(".mp3") || path.toString().endsWith(".flac"))
                            .toList();

                    List<Path> moviePaths = paths.parallelStream()
                            .filter(path -> path.toString().endsWith(".mp4") || path.toString().endsWith(".mkv") || path.toString().endsWith(".avi") || path.toString().endsWith(".mpeg"))
                            .toList();

                    List<Movie> finalMovies;
                    List<Music> finalMusic;
                    try {
                        finalMovies = createMovieEntities(moviePaths);
                        finalMusic = createMusicEntities(musicPaths);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    mediaLibrary.setMovies(finalMovies);
                    mediaLibrary.setMusic(finalMusic);

                    // Save entities to the database asynchronously
                    CompletableFuture<Void> moviesFuture = CompletableFuture.runAsync(() ->
                                    movieRepository.saveAll(finalMovies))
                            .thenRun(() -> log.info("Finished Indexing Movies"));
                    CompletableFuture<Void> musicFuture = CompletableFuture.runAsync(() ->
                                    musicRepository.saveAll(finalMusic))
                            .thenRun(() -> log.info("Finished Indexing Music"));

                    // Return a new CompletableFuture that is completed when both of the provided CompletableFutures complete
                    return CompletableFuture.allOf(moviesFuture, musicFuture);
                })
                .exceptionally(throwable -> {
                    log.error("Error indexing media", throwable);
                    return null;
                });
    }

    public CompletableFuture<List<Path>> findLocalMediaFiles(String... locations) throws IOException {
        final String pattern = "glob:**/*.{mp4,mpeg,mp3,mkv,flac}";

        List<Path> matchingPaths = new ArrayList<>();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pattern);

        for (String location : locations) {

            Path start = Paths.get(location);

            if (Files.exists(start)) {

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

        }

        return CompletableFuture.completedFuture(matchingPaths);
    }


    private List<Movie> createMovieEntities(List<Path> paths) throws IOException {

        List<Movie> moviesList = new ArrayList<>();

        for (Path entry : paths) {
            Movie movie = new Movie();
            log.debug(entry.toString());

            // Get the immediate parent folder name
            String parentFolderName = entry.getParent().getFileName().toString();

            String encodedFileName = decodePathSegment.apply(entry.getFileName().toString());
            String contentStoreDir;
            if (parentFolderName.equals("Movies")) {
                // If the parent folder is also /Music, use the SPRING_CONTENT_MUSIC_STORE directly
                contentStoreDir = runtimeHelper.getMoviesContentStore() + encodedFileName;
            } else {
                // Construct storeDir by appending parentFolderName and file.getName()
                contentStoreDir = runtimeHelper.getMoviesContentStore() + parentFolderName + File.separator + encodedFileName;
            }

            String userDir = runtimeHelper.getUserHomePath() + File.separator + contentStoreDir;
            log.debug("Content Store {}", contentStoreDir);
            log.debug("Local Directory {}", userDir);

            movie.setName(encodedFileName);
            movie.setContentLength(Files.size(entry));
            movie.setSummary(entry.getFileName().toString());
            movie.setContentId(decodePathSegment.apply(contentStoreDir));
            movie.setContentMimeType(decodeContentType.apply(entry));
            movie.setMovieCode(encodedFileName);
            movie.setMediaSource(ApplicationConstants.LOCAL_MEDIA);
            moviesList.add(movie);
        }

        return moviesList;
    }

    private List<Music> createMusicEntities(List<Path> paths) throws IOException {

        List<Music> musicList = new ArrayList<>();

        for (Path entry : paths) {
            Music music = new Music();
            log.debug(entry.toString());

            // Get the immediate parent folder name
            String parentFolderName = entry.getParent().getFileName().toString();

            String encodedFileName = decodePathSegment.apply(entry.getFileName().toString());
            String contentStoreDir;
            if (parentFolderName.equals("Music")) {
                // If the parent folder is also /Music, use the SPRING_CONTENT_MUSIC_STORE directly
                contentStoreDir = runtimeHelper.getMusicContentStore() + encodedFileName;
            } else {
                // Construct storeDir by appending parentFolderName and file.getName()
                contentStoreDir = runtimeHelper.getMusicContentStore() + parentFolderName + File.separator + encodedFileName;
            }

            String userDir = runtimeHelper.getUserHomePath() + File.separator + contentStoreDir;
            log.debug("Content Store {}", contentStoreDir);
            log.debug("Local Directory {}", userDir);

            music.setName(encodedFileName);
            music.setContentLength(Files.size(entry));
            music.setSummary(entry.getFileName().toString());
            music.setContentId(decodePathSegment.apply(contentStoreDir));
            music.setContentMimeType(decodeContentType.apply(entry));
            music.setMusicId(encodedFileName);
            music.setMediaSource(ApplicationConstants.LOCAL_MEDIA);
            musicList.add(music);
        }

        return musicList;
    }

    UnaryOperator<String> decodePathSegment = pathSegment -> UriUtils.decode(pathSegment, StandardCharsets.UTF_8.name());
    Function<Path, String> decodeContentType = fileEntryPath -> MediaTypeFactory.getMediaType(new FileSystemResource(fileEntryPath)).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();

}
