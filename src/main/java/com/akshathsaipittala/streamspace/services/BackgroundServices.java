package com.akshathsaipittala.streamspace.services;

import com.akshathsaipittala.streamspace.entity.*;
import com.akshathsaipittala.streamspace.indexer.MediaIndexer;
import com.akshathsaipittala.streamspace.repository.DownloadTaskRepository;
import com.akshathsaipittala.streamspace.repository.UserPreferences;
import com.akshathsaipittala.streamspace.utils.RuntimeHelper;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BackgroundServices {

    @Lazy
    @Autowired
    private RuntimeHelper runtimeHelper;

    @Lazy
    @Autowired
    private MediaIndexer mediaIndexer;

    @Lazy
    @Autowired
    private DownloadTaskRepository downloadTasksRepo;

    @Lazy
    @Autowired
    private TorrentDownloadService torrentDownloadService;

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

            mediaIndexer.indexLocalMedia(
                            runtimeHelper.getMediaFolders().get(CONTENTTYPE.VIDEO),
                            runtimeHelper.getMediaFolders().get(CONTENTTYPE.AUDIO)
                    ).thenRunAsync(this::startBackgroundDownloads)
                    .exceptionally(throwable -> {
                        log.error("Error during media indexing or starting background downloads", throwable);
                        return null;
                    });
        } catch (IOException e) {
            log.error("Error indexing local media", e);
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

        Specification<DownloadTask> spec = DownloadTaskSpecs.hasTaskStatusIn(STATUS.RETRY, STATUS.NEW);
        List<DownloadTask> downloadTasks = new ArrayList<>(downloadTasksRepo.findAll(spec));

        // Starts all downloads on startup
        if (!downloadTasks.isEmpty()) {
            downloadTasks.forEach(downloadTask -> torrentDownloadService.startDownload(downloadTask));

            // Future Plan targeted for StructuredConcurrency Final release
            // runAsStructuredConcurrent(downloadTasks);
        }

    }

    /* private void runAsStructuredConcurrent(List<BackgroundDownloadTask> backgroundDownloadTasks) {
        ConJob conJob = new ConJob();
        conJob.executeAll(backgroundDownloadTasks);
    }*/

    @PreDestroy
    private void onShutDown() {
        List<DownloadTask> downloadTasks = downloadTasksRepo.findAll();
        if (downloadTasks.isEmpty()) {
            log.info("No pending Downloads");
        } else {
            log.info("Setting pending Downloads to retry");
            downloadTasks.forEach(downloadTask -> downloadTask.setTaskStatus(STATUS.RETRY));
            downloadTasksRepo.saveAll(downloadTasks);
        }
    }

}
