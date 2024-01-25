package com.akshathsaipittala.streamspace.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Getter
@Setter
@Primary
@Component
public class RuntimeHelper {

    private String userHomePath = System.getProperty("user.home");
    private String downloadsFolderPath;
    private String musicFolderPath;
    private String moviesFolderPath;
    private String musicContentStore;
    private String moviesContentStore;

    @EventListener(ApplicationReadyEvent.class)
    void onApplicationStartup() {
        String os = System.getProperty("os.name").toLowerCase();
        log.info("OS {}", os);

        if (os.contains("win")) {
            downloadsFolderPath = userHomePath + File.separator + "Downloads";
            musicFolderPath = userHomePath + File.separator + ApplicationConstants.MUSIC;
            moviesFolderPath = userHomePath + File.separator + "Videos";
            musicContentStore = ApplicationConstants.MUSIC + File.separator;
            moviesContentStore = "Videos" + File.separator;
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
            //Need to revisit for linux
            downloadsFolderPath = userHomePath + File.separator + "Downloads";
            musicFolderPath = userHomePath + File.separator + ApplicationConstants.MUSIC;
            moviesFolderPath = userHomePath + File.separator + "Movies";
            musicContentStore = ApplicationConstants.MUSIC + File.separator;
            moviesContentStore = "Movies" + File.separator;
        } else {
            log.info("Unknown OS file folder structure! Using user.home");
            downloadsFolderPath = userHomePath;
            musicFolderPath = userHomePath;
            moviesFolderPath = userHomePath;
            musicContentStore = userHomePath;
            moviesContentStore = userHomePath;
        }

        log.info("Final locations paths");
        log.info("Downloads: {}", downloadsFolderPath);
        log.info("MUSIC: {}", musicContentStore);
        log.info("MOVIES: {}", moviesContentStore);
    }

}
