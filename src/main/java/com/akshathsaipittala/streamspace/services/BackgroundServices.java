package com.akshathsaipittala.streamspace.services;

import com.akshathsaipittala.streamspace.helpers.*;
import com.akshathsaipittala.streamspace.library.Indexer;
import com.akshathsaipittala.streamspace.repository.Downloads;
import com.akshathsaipittala.streamspace.repository.UserPreferences;
import com.akshathsaipittala.streamspace.torrentengine.TorrentDownloadManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class BackgroundServices {

    @Lazy
    @Autowired
    private Indexer indexer;

    @Lazy
    @Autowired
    private Downloads downloadTasksRepo;

    @Lazy
    @Autowired
    private TorrentDownloadManager torrentDownloadManager;

    @Lazy
    @Autowired
    private UserPreferences userPreferences;

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        try {
            log.info("Indexing Local Media");
            // Runs only during initial setup
            if (userPreferences.count() == 0) {
                configurePreferences();
            }

            indexer.indexLocalMedia(new HashSet<>(ContentDirectoryServices.mediaFolders.values())
                    ).thenRunAsync(this::startBackgroundDownloads)
                    .exceptionally(throwable -> {
                        log.error("Error during media indexing or starting background downloads", throwable);
                        return null;
                    });
        } catch (IOException e) {
            log.error("Error indexing local media {}", e.getMessage(), e);
        }
    }

    private void configurePreferences() {
        List<Preference> features = List.of(
                new Preference().setPrefId(1).setName("DARK_MODE_ENABLED")
        );
        userPreferences.saveAll(features);
    }

    @Async
    public void startBackgroundDownloads() {
        var downloadTasks = new ArrayList<>(downloadTasksRepo.findAll());
        if (!downloadTasks.isEmpty()) {
            log.info("Starting background downloads");
            downloadTasks.forEach(downloadTask -> torrentDownloadManager.startDownload(downloadTask));
            // Future Plan targeted for StructuredConcurrency Final release
            // runAsStructuredConcurrent(downloadTasks);
        }
    }

    /* private void runAsStructuredConcurrent(List<BackgroundDownloadTask> backgroundDownloadTasks) {
        ConJob conJob = new ConJob();
        conJob.executeAll(backgroundDownloadTasks);
    }*/
}
