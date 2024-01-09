package com.akshathsaipittala.streamspace.services;

import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.runtime.BtClient;
import com.akshathsaipittala.streamspace.entity.CONTENTTYPE;
import com.akshathsaipittala.streamspace.entity.DOWNLOADTYPE;
import com.akshathsaipittala.streamspace.entity.DownloadTask;
import com.akshathsaipittala.streamspace.entity.STATUS;
import com.akshathsaipittala.streamspace.repository.DownloadTaskRepository;
import com.akshathsaipittala.streamspace.utils.RuntimeHelper;
import com.akshathsaipittala.streamspace.utils.TorrentProgressHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class TorrentDownloadService {

    final RuntimeHelper runtimeHelper;
    final DownloadTaskRepository downloadTaskRepository;
    final TorrentProgressHandler torrentProgressHandler;
    final RandomizedDownloader randomizedDownloader;
    final SequentialDownloader sequentialDownloader;

    @Async
    public void startDownload(DownloadTask downloadTask) {
        BtClient client;
        Storage storage;

        if (downloadTask.getMediaType().equals(CONTENTTYPE.VIDEO)) {
            storage = new FileSystemStorage(new File(runtimeHelper.MOVIES_FOLDER).toPath());
        } else {
            storage = new FileSystemStorage(new File(runtimeHelper.MUSIC_FOLDER).toPath());
        }

        if (downloadTask.getDownloadType().equals(DOWNLOADTYPE.SEQUENTIAL)) {
            client = sequentialDownloader.createSequentialDownloadClient(downloadTask, storage);
        } else {
            client = randomizedDownloader.createRandomSelectorClient(downloadTask, storage);
        }

        downloadTask.setTaskStatus(STATUS.INPROGRESS);
        downloadTaskRepository.save(downloadTask);

        client.startAsync(state -> {
            if (state.getPiecesRemaining() == 0) {
                log.info("Started as Seed: {}", state.startedAsSeed());
                log.debug("Download finished!");
                downloadTaskRepository.delete(downloadTask);  // remove task from database

                log.debug("{}", state.getConnectedPeers());
                int completed = state.getPiecesComplete();
                double completePercents = getCompletePercentage(state.getPiecesTotal(), completed);
                log.debug("{}", completePercents);

                long downloaded = state.getDownloaded();
                long uploaded = state.getUploaded();
                int peerCount = state.getConnectedPeers().size();
                log.debug("Downloaded {}, Uploaded {}, Peer Count {}", readableSize(downloaded), readableSize(uploaded), peerCount);

                try {

                    //Close WebSocket connection after download's complete
                    WebSocketSession webSocketSession = torrentProgressHandler.getSessions().get(downloadTask.getTorrentHash());
                    if (webSocketSession.isOpen()) {
                        webSocketSession.close();
                        log.info("Closed WebSocket connection");
                    }

                } catch (IOException e) {
                    log.error("Error closing WebSocket Connection " + e);
                }

            } else {
                //task.setProgress(percentage);  // update task progress
                try {
                    //log.debug("{}", state.getConnectedPeers());
                    int completed = state.getPiecesComplete();
                    double completePercents = getCompletePercentage(state.getPiecesTotal(), completed);
                    //log.debug("{}", completePercents);

                    long downloaded = state.getDownloaded();
                    long uploaded = state.getUploaded();
                    int peerCount = state.getConnectedPeers().size();
                    //log.debug("Downloaded {}, Uploaded {}, Peer Count {}", readableSize(downloaded), readableSize(uploaded), peerCount);

                    //String remainingTime = getRemainingTime(downloaded, state.getPiecesRemaining(), state.getPiecesNotSkipped());
                    //log.debug("{}", remainingTime);

                    torrentProgressHandler.sendProgressUpdate(downloadTask.getTorrentHash(), String.format("%.2f%%", completePercents), readableSize(downloaded), readableSize(uploaded), peerCount);
                } catch (IOException e) {
                    log.error("Error occurred during download " + e);
                }
            }
        }, 5000);

    }

    private double getCompletePercentage(int total, int completed) {
        return completed / ((double) total) * 100;
    }

    public static String readableSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
