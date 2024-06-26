package com.akshathsaipittala.streamspace.indexer;

import bt.metainfo.TorrentFile;
import bt.metainfo.TorrentId;
import com.akshathsaipittala.streamspace.entity.Movie;
import com.akshathsaipittala.streamspace.entity.Song;
import com.akshathsaipittala.streamspace.repository.MovieRepository;
import com.akshathsaipittala.streamspace.repository.MusicRepository;
import com.akshathsaipittala.streamspace.utils.ApplicationConstants;
import com.akshathsaipittala.streamspace.utils.HelperFunctions;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaIndexer {

    private final UnaryOperator<String> decodePathSegment = pathSegment -> UriUtils.decode(pathSegment, StandardCharsets.UTF_8.name());
    private final Function<Path, String> decodeContentType = fileEntryPath -> MediaTypeFactory.getMediaType(new FileSystemResource(fileEntryPath)).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
    final RuntimeHelper runtimeHelper;
    final MovieRepository movieRepository;
    final MusicRepository musicRepository;

    @Async
    public void indexMovie(TorrentFile file, String torrentName, String fileName, TorrentId torrentId) {
        log.info("FileName {}", fileName);
        log.info("TorrentName {}", torrentName);

        Movie movie = movieRepository.findByName(fileName);

        if (movie != null) {
            log.info("Movie Found {}", movie);
            movieRepository.delete(movie);
            // TODO: need to revisit
            // Primary key cannot be updated due to which new record is created
            // hence deleting and inserting as new with latest torrentId
            movie.setMovieCode(torrentId.toString().toUpperCase());
            log.info("{} already indexed", fileName);
            movieRepository.save(movie);
        } else {
            movie = new Movie()
                    .setContentLength(file.getSize())
                    .setName(fileName)
                    .setSummary(fileName)
                    .setContentMimeType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .setContentId(runtimeHelper.getMoviesContentStore() + torrentName + "/" + fileName)
                    .setMovieCode(torrentId.toString().toUpperCase())
                    .setMediaSource(ApplicationConstants.TORRENT);

            log.info("Content ID {}", runtimeHelper.getMoviesContentStore() + torrentName + "/" + fileName);
            movieRepository.save(movie);
        }

    }

    @Async
    public void indexMusic(TorrentFile file, String torrentName, String fileName, TorrentId torrentId) {
        log.info("FileName {}", fileName);
        log.info("TorrentName {}", torrentName);
        Song song = new Song();
        song.setContentLength(file.getSize());
        song.setName(fileName);
        song.setSummary(fileName);
        song.setContentMimeType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        log.info(runtimeHelper.getMoviesContentStore() + torrentName + "/" + fileName);
        song.setContentId(runtimeHelper.getMoviesContentStore() + torrentName + "/" + fileName);
        song.setSongId(torrentId.toString().toUpperCase());
        song.setMediaSource(ApplicationConstants.TORRENT);
        musicRepository.save(song);
    }

    /**
     * Concurrent indexer
     */
    @Async
    public CompletableFuture<Void> indexLocalMedia(String... locations) throws IOException {
        return findLocalMediaFiles(locations)
                .thenCompose(paths -> {
                    List<Path> musicPaths = filterPaths(paths, ".mp3", ".flac");
                    List<Path> moviePaths = filterPaths(paths, ".mp4", ".mkv", ".avi", ".mpeg");

                    try {
                        List<Movie> finalMovies = createMovieEntities(moviePaths);
                        List<Song> finalSongs = createMusicEntities(musicPaths);

                        CompletableFuture<Void> moviesFuture = saveMoviesAsync(finalMovies);
                        CompletableFuture<Void> musicFuture = saveMusicAsync(finalSongs);

                        return CompletableFuture.allOf(moviesFuture, musicFuture);
                    } catch (IOException e) {
                        log.error("Error creating media entities", e);
                        return CompletableFuture.failedFuture(e);
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error indexing media", throwable);
                    return null;
                });
    }

    private CompletableFuture<List<Path>> findLocalMediaFiles(String... locations) {
        final String pattern = "glob:**/*.{mp4,mpeg,mp3,mkv,flac}";

        List<Path> matchingPaths = new ArrayList<>();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pattern);

        for (String location : locations) {
            try {
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
            } catch (IOException e) {
                log.error("Error finding personal media files in location: {}", location, e);
            }
        }

        return CompletableFuture.completedFuture(matchingPaths);
    }

    private List<Path> filterPaths(List<Path> paths, String... extensions) {
        return paths.parallelStream()
                .filter(path -> {
                    String pathString = path.toString().toLowerCase();
                    for (String ext : extensions) {
                        if (pathString.endsWith(ext)) {
                            return true;
                        }
                    }
                    return false;
                })
                .toList();
    }

    private CompletableFuture<Void> saveMoviesAsync(List<Movie> movies) {
        return CompletableFuture.runAsync(() ->
                        movies.stream()
                                .filter(movie -> !movieRepository.existsByContentId(movie.getContentId()))
                                .forEach(movieRepository::save))
                .thenRun(() -> log.info("Finished Indexing Movies"));
    }

    private CompletableFuture<Void> saveMusicAsync(List<Song> songs) {
        return CompletableFuture.runAsync(() ->
                        songs.stream()
                                .filter(song -> !musicRepository.existsByContentId(song.getContentId()))
                                .forEach(musicRepository::save))
                .thenRun(() -> log.info("Finished Indexing Music"));
    }

    private List<Movie> createMovieEntities(List<Path> paths) throws IOException {

        List<Movie> moviesList = new ArrayList<>();

        for (Path entry : paths) {
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

            Movie movie = new Movie()
                    .setName(encodedFileName)
                    .setContentLength(Files.size(entry))
                    .setSummary(entry.getFileName().toString())
                    .setContentId(decodePathSegment.apply(contentStoreDir))
                    .setContentMimeType(decodeContentType.apply(entry))
                    .setMovieCode(HelperFunctions.generateUniqueCode())
                    .setMediaSource(ApplicationConstants.LOCAL_MEDIA);

            moviesList.add(movie);
        }

        return moviesList;
    }

    private List<Song> createMusicEntities(List<Path> paths) throws IOException {

        List<Song> songs = new ArrayList<>();

        for (Path entry : paths) {
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

            Song song = new Song();
            song.setName(encodedFileName);
            song.setContentLength(Files.size(entry));
            song.setSummary(entry.getFileName().toString());
            song.setContentId(decodePathSegment.apply(contentStoreDir));
            song.setContentMimeType(decodeContentType.apply(entry));
            song.setSongId(encodedFileName);
            song.setMediaSource(ApplicationConstants.LOCAL_MEDIA);
            songs.add(song);
        }

        return songs;
    }

}
