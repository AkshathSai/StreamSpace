package com.akshathsaipittala.streamspace.torrentengine;

import com.akshathsaipittala.streamspace.helpers.DOWNLOADTYPE;
import com.akshathsaipittala.streamspace.library.Indexer;
import com.akshathsaipittala.streamspace.downloads.Downloads;
import com.akshathsaipittala.streamspace.services.ContentDirectoryServices;
import com.akshathsaipittala.streamspace.helpers.DownloadTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TorrentDownloadManager {

    public static final int BitTorrentPort = 6891;
    private static final Map<String, TorrentClient> clients = new ConcurrentHashMap<>();
    final Downloads downloads;
    final Indexer indexer;
    final DownloadProgressHandler downloadProgressHandler;

    public void startDownload(DownloadTask downloadTask) {
        if (!downloads.existsById(downloadTask.getTorrentHash())) {
            TorrentClient torrentClient;
            try {
                torrentClient = clients.get(downloadTask.getTorrentHash());

                if (torrentClient != null) {
                    torrentClient.resume();
                } else {
                    torrentClient = new TorrentClient(
                            downloadTaskToOptions(downloadTask),
                            indexer,
                            downloadProgressHandler,
                            this); // Passing current instance
                    clients.put(downloadTask.getTorrentHash(), torrentClient);
                    torrentClient.resume();
                }
                downloads.save(downloadTask);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            clients.get(downloadTask.getTorrentHash()).resume();
        }
    }

    public void startAllPendingDownloads() {
        var downloadTasks = new ArrayList<>(downloads.findAll());
        if (!downloadTasks.isEmpty()) {
            log.info("Starting background downloads");
            downloadTasks.forEach(this::startDownload);
        }
    }

    public void pauseDownload(String torrentHash) {
        clients.get(torrentHash).pause();
    }

    public void onComplete(String torrentHash) {
        downloads.deleteById(torrentHash);
        var torrentClient = clients.get(torrentHash);
        if (torrentClient.client.isStarted()) {
            torrentClient.client.stop();
            log.info("Torrent Client has been stopped {}", torrentHash);
        }
        clients.remove(torrentHash);
    }

    public void cancelDownload(String torrentHash) {
        clients.get(torrentHash).pause();
        downloads.deleteById(torrentHash);
        clients.remove(torrentHash);
    }

    private Options downloadTaskToOptions(DownloadTask downloadTask) {
        Options options = new Options();
        // options.setMetainfoFile(null);
        options.setTorrentHash(downloadTask.getTorrentHash());
        options.setMagnetUri(createMagnetUri(downloadTask.getTorrentHash()));
        options.setTargetDirectory(new File(ContentDirectoryServices.mediaFolders.get(downloadTask.getMediaType())));
        options.setSeedAfterDownloaded(false);
        options.setSequential(downloadTask.getDownloadType() == DOWNLOADTYPE.SEQUENTIAL);
        options.setEnforceEncryption(true);
        options.setDisableUi(true);
        options.setDisableTorrentStateLogs(false);
        options.setVerboseLogging(false);
        options.setTraceLogging(false);
        // options.setIface(null);
        options.setPort(BitTorrentPort);
        // options.setDhtPort(null);
        options.setDownloadAllFiles(true);
        log.info("{}", options);
        return options;
    }

    private static String createMagnetUri(String torrentHash) {
        return "magnet:?xt=urn:btih:" + torrentHash;
    }

}
