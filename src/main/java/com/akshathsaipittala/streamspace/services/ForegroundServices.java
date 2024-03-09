package com.akshathsaipittala.streamspace.services;

import com.akshathsaipittala.streamspace.entity.CONTENTTYPE;
import com.akshathsaipittala.streamspace.entity.DownloadTask;
import com.akshathsaipittala.streamspace.entity.STATUS;
import com.akshathsaipittala.streamspace.indexer.MediaIndexer;
import com.akshathsaipittala.streamspace.repository.DownloadTaskRepository;
import com.akshathsaipittala.streamspace.services.torrentengine.Options;
import com.akshathsaipittala.streamspace.utils.RuntimeHelper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Async
@Slf4j
@Service
public class ForegroundServices {

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

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    BackgroundDownloadTask backgroundDownloadTask;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {

        Thread.startVirtualThread(() -> {
            try {
                log.info("Indexing Local Media");
                mediaIndexer.indexLocalMedia(
                        runtimeHelper.getMediaFolders().get(CONTENTTYPE.VIDEO),
                        runtimeHelper.getMediaFolders().get(CONTENTTYPE.AUDIO)
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        startBackgroundDownloads();

        // 3C88F31D82729DBF83A702BD536A376B23DB5EC6
    }

    private void startBackgroundDownloads() {
        // Starts all downloads on startup
        List<DownloadTask> downloadTasks = new ArrayList<>();
        downloadTasks.addAll(downloadTasksRepo.findAllByTaskStatus(STATUS.RETRY));
        downloadTasks.addAll(downloadTasksRepo.findAllByTaskStatus(STATUS.NEW));

        downloadTasks.forEach(downloadTask -> {
            Options options = torrentDownloadService.downloadTaskToOptions(downloadTask);
            jobScheduler.enqueue(() -> backgroundDownloadTask.startDownload(options));
            downloadTask.setTaskStatus(STATUS.INPROGRESS);
        });

        // runAsStructuredConcurrent(downloadTasks);
        downloadTasksRepo.saveAll(downloadTasks);
    }

    /*private void runAsStructuredConcurrent(List<BackgroundDownloadTask> backgroundDownloadTasks) {
        ConJob conJob = new ConJob();
        conJob.executeAll(backgroundDownloadTasks);
    }*/

    @PostConstruct
    private void onShutDown() {
        List<DownloadTask> downloadTasks = downloadTasksRepo.findAllByTaskStatus(STATUS.INPROGRESS);
        downloadTasks.forEach(downloadTask -> downloadTask.setTaskStatus(STATUS.RETRY));
        downloadTasksRepo.saveAll(downloadTasks);
    }

}
