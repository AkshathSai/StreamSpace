package com.akshathsaipittala.streamspace.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@Primary
@Component
public class RuntimeHelper {

    public final String USER_HOME = System.getProperty("user.home");
    public String DOWNLOADS_FOLDER;
    public String MUSIC_FOLDER;
    public String MOVIES_FOLDER;
    public String SPRING_CONTENT_MUSIC_STORE;
    public String SPRING_CONTENT_MOVIES_STORE;

    // Define a method to generate a unique movieCode (e.g., using UUID)
    public static String generateUniqueCode() {
        return UUID.randomUUID().toString();
    }

    @EventListener(ApplicationReadyEvent.class)
    void onApplicationStartup() {
        String os = System.getProperty("os.name").toLowerCase();
        log.info("OS {}", os);
        String downloadsFolder;
        String musicFolder;
        String moviesFolder;
        String musicStore;
        String movieStore;

        if (os.contains("win")) {
            downloadsFolder = USER_HOME + File.separator + "Downloads";
            musicFolder = USER_HOME + File.separator + ApplicationConstants.MUSIC;
            moviesFolder = USER_HOME + File.separator + "Videos";
            musicStore = ApplicationConstants.MUSIC + File.separator;
            movieStore = "Videos" + File.separator;
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
            //Need to revisit for linux
            downloadsFolder = USER_HOME + File.separator + "Downloads";
            musicFolder = USER_HOME + File.separator + ApplicationConstants.MUSIC;
            moviesFolder = USER_HOME + File.separator + "Movies";
            musicStore = ApplicationConstants.MUSIC + File.separator;
            movieStore = "Movies" + File.separator;
        } else {
            log.info("Unknown OS file folder structure! Using user.home");
            downloadsFolder = USER_HOME;
            musicFolder = USER_HOME;
            moviesFolder = USER_HOME;
            musicStore = USER_HOME;
            movieStore = USER_HOME;
        }

        DOWNLOADS_FOLDER = downloadsFolder;
        SPRING_CONTENT_MUSIC_STORE = musicStore;
        SPRING_CONTENT_MOVIES_STORE = movieStore;
        MUSIC_FOLDER = musicFolder;
        MOVIES_FOLDER = moviesFolder;

        log.info("Final locations paths");
        log.info("Downloads: {}", DOWNLOADS_FOLDER);
        log.info("MUSIC: {}", SPRING_CONTENT_MUSIC_STORE);
        log.info("MOVIES: {}", SPRING_CONTENT_MOVIES_STORE);
    }

}
