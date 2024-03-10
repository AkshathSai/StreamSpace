package com.akshathsaipittala.streamspace.listener;

import com.akshathsaipittala.streamspace.entity.DownloadTask;
import com.akshathsaipittala.streamspace.services.background.TorrentDownloadService;
import jakarta.persistence.PostPersist;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DownloadTaskListener {

    @Async
    @PostPersist
    public void onReceivingDownloadTask(DownloadTask downloadTask) {
        TorrentDownloadService torrentDownloadService = ApplicationContextProvider.getApplicationContext().getBean(TorrentDownloadService.class);
        torrentDownloadService.startDownload(downloadTask);
    }

}