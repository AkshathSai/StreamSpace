package com.akshathsaipittala.streamspace.services.background;

import com.akshathsaipittala.streamspace.entity.CONTENTTYPE;
import com.akshathsaipittala.streamspace.entity.DownloadTask;
import com.akshathsaipittala.streamspace.entity.STATUS;
import com.akshathsaipittala.streamspace.entity.DownloadTaskSpecs;
import com.akshathsaipittala.streamspace.indexer.MediaIndexer;
import com.akshathsaipittala.streamspace.repository.DownloadTaskRepository;
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

@Async
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

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {

        Thread.startVirtualThread(() -> {
            try {
                log.info("Indexing Local Media");
                mediaIndexer.indexLocalMedia(
                        runtimeHelper.getMediaFolders().get(CONTENTTYPE.VIDEO),
                        runtimeHelper.getMediaFolders().get(CONTENTTYPE.AUDIO)
                );
                startBackgroundDownloads();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void startBackgroundDownloads() {

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
