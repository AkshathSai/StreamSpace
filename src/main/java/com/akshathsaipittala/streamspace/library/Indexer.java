package com.akshathsaipittala.streamspace.library;

import bt.metainfo.TorrentFile;
import bt.metainfo.TorrentId;
import com.akshathsaipittala.streamspace.services.ContentDirectoryServices;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Slf4j
@Service
@RequiredArgsConstructor
public class Indexer {

    private final UnaryOperator<String> decodePathSegment = pathSegment -> UriUtils.decode(pathSegment, StandardCharsets.UTF_8.name());
    private final Function<Path, String> decodeContentType = fileEntryPath -> MediaTypeFactory.getMediaType(new FileSystemResource(fileEntryPath)).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
    final ContentDirectoryServices contentDirectoryServices;
    final VideoRepository videoRepository;
    final MusicRepository musicRepository;

    public void indexMovie(TorrentFile file, String torrentName, String fileName, TorrentId torrentId) {
        log.info("FileName {}", fileName);
        log.info("TorrentName {}", torrentName);

        Video video = videoRepository.findByName(fileName);

        if (video != null) {
            log.info("Movie Found {}", video);
            videoRepository.delete(video);
            // TODO: need to revisit
            // Primary key cannot be updated due to which new record is created
            // hence deleting and inserting as new with latest torrentId
            video.setMovieCode(torrentId.toString().toUpperCase());
            log.info("{} already indexed", fileName);
            videoRepository.save(video);
        } else {
            video = new Video()
                    .setContentLength(file.getSize())
                    .setName(fileName)
                    .setCreated(LocalDateTime.now())
                    .setSummary(fileName)
                    .setContentMimeType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .setContentId(contentDirectoryServices.getMoviesContentStore() + torrentName + "/" + fileName)
                    .setMovieCode(torrentId.toString().toUpperCase())
                    .setSource(SOURCE.TORRENT);

            log.info("Content ID {}", contentDirectoryServices.getMoviesContentStore() + torrentName + "/" + fileName);
            videoRepository.save(video);
        }

    }

    public void indexMusic(TorrentFile file, String torrentName, String fileName, TorrentId torrentId) {
        log.info("FileName {}", fileName);
        log.info("TorrentName {}", torrentName);
        Song song = new Song();
        song.setContentLength(file.getSize());
        song.setName(fileName);
        song.setSummary(fileName);
        song.setContentMimeType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        log.info(contentDirectoryServices.getMoviesContentStore() + torrentName + "/" + fileName);
        song.setContentId(contentDirectoryServices.getMoviesContentStore() + torrentName + "/" + fileName);
        song.setSongId(torrentId.toString().toUpperCase());
        song.setSource(SOURCE.TORRENT);
        musicRepository.save(song);
    }

    /**
     * Concurrent indexer
     */
    @Async
    public CompletableFuture<Void> indexLocalMedia(Set<String> locations) {
        return findLocalMediaFiles(locations)
                .thenCompose(paths -> {
                    List<Path> musicPaths = filterPaths(paths, ".mp3", ".flac");
                    List<Path> videoFolderPaths = filterPaths(paths, ".mp4", ".mkv", ".avi", ".mpeg");

                    try {
                        List<Video> finalVideos = createVideoEntities(videoFolderPaths);
                        List<Song> finalSongs = createMusicEntities(musicPaths);

                        CompletableFuture<Void> videosFuture = saveVideosAsync(finalVideos);
                        CompletableFuture<Void> musicFuture = saveMusicAsync(finalSongs);

                        return CompletableFuture.allOf(videosFuture, musicFuture);
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

    private CompletableFuture<List<Path>> findLocalMediaFiles(Set<String> locations) {
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

    private CompletableFuture<Void> saveVideosAsync(List<Video> videos) {
        return CompletableFuture.runAsync(() -> {
            List<Video> nonExistingVideos = videos.stream()
                    .filter(video -> !videoRepository.existsByContentId(video.getContentId()))
                    .toList();
            videoRepository.saveAll(nonExistingVideos);
        }).thenRun(() -> log.info("Finished Indexing Videos"));

    }

    private CompletableFuture<Void> saveMusicAsync(List<Song> songs) {
        return CompletableFuture.runAsync(() -> {
            List<Song> nonExistingSongs = songs.stream()
                    .filter(song -> !musicRepository.existsByContentId(song.getContentId()))
                    .toList();
            musicRepository.saveAll(nonExistingSongs);
        }).thenRun(() -> log.info("Finished Indexing Music"));
    }

    private List<Video> createVideoEntities(List<Path> paths) throws IOException {
        Video video = null;
        List<Video> videos = new ArrayList<>();
        String encodedFileName;
        Path relativePath;

        for (Path entry : paths) {
            log.debug(entry.toString());
            encodedFileName = decodePathSegment.apply(entry.getFileName().toString());

            // Relativize the entry path against the user home directory
            relativePath = Paths.get(ContentDirectoryServices.userHomePath).relativize(entry);

            video = new Video()
                    .setName(encodedFileName)
                    .setContentLength(Files.size(entry))
                    .setSummary(entry.getFileName().toString())
                    .setContentId(File.separator + decodePathSegment.apply(relativePath.toString()))
                    //.setContentId(decodePathSegment.apply(relativePath.toString()))
                    .setContentMimeType(decodeContentType.apply(entry))
                    //.setMovieCode(generateUniqueCode())
                    .setMovieCode(encodedFileName)
                    .setSource(SOURCE.LOCAL);

            videos.add(video);
        }

        return videos;
    }

    private List<Song> createMusicEntities(List<Path> paths) throws IOException {
        Song song = null;
        List<Song> songs = new ArrayList<>();
        Path relativePath;
        String encodedFileName;

        for (Path entry : paths) {
            log.debug(entry.toString());
            encodedFileName = decodePathSegment.apply(entry.getFileName().toString());

            // Relativize the entry path against the user home directory
            relativePath = Paths.get(ContentDirectoryServices.userHomePath).relativize(entry);

            song = new Song()
                    .setName(encodedFileName)
                    .setContentLength(Files.size(entry))
                    .setSummary(entry.getFileName().toString())
                    .setContentId(File.separator + decodePathSegment.apply(relativePath.toString()))
                    //.setContentId(decodePathSegment.apply(relativePath.toString()))
                    .setContentMimeType(decodeContentType.apply(entry))
                    .setSongId(encodedFileName)
                    .setSource(SOURCE.LOCAL);

            songs.add(song);
        }

        return songs;
    }
}

enum SOURCE {
    LOCAL, WEB, TORRENT
}