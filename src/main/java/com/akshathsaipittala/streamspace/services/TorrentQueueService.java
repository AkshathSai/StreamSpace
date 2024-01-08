package com.akshathsaipittala.streamspace.services;

import com.akshathsaipittala.streamspace.entity.DownloadTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class TorrentQueueService {
    private Queue<DownloadTask> queue = new ConcurrentLinkedQueue<>();

    public void addToQueue(DownloadTask downloadTask) {
        log.info("Download Task {}", downloadTask);
        queue.add(downloadTask);
    }

    public DownloadTask getFromQueue() {
        return queue.poll();
    }
}

