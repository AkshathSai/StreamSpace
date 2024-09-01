package com.akshathsaipittala.streamspace.torrentengine;

import com.akshathsaipittala.streamspace.helpers.DOWNLOADTYPE;
import com.akshathsaipittala.streamspace.helpers.STATUS;
import com.akshathsaipittala.streamspace.library.Indexer;
import com.akshathsaipittala.streamspace.downloads.Downloads;
import com.akshathsaipittala.streamspace.helpers.ApplicationConstants;
import com.akshathsaipittala.streamspace.services.ContentDirectoryServices;
import com.akshathsaipittala.streamspace.helpers.DownloadTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TorrentDownloadManager {

    private static final Map<String, TorrentClient> clients = new ConcurrentHashMap<>();
    private static final Random random = new Random();
    final Downloads downloads;
    final Indexer indexer;
    final DownloadProgressHandler downloadProgressHandler;

    public void startDownload(DownloadTask downloadTask) {
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
                        this, // Passing current instance
                        downloadTask.getTorrentHash());
                clients.put(downloadTask.getTorrentHash(), torrentClient);
                torrentClient.resume();
            }

            downloadTask.setTaskStatus(STATUS.INPROGRESS);
            downloads.save(downloadTask);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void pauseDownload(String torrentHash) {
        clients.get(torrentHash).pause();
        downloads.findById(torrentHash).ifPresent(download -> {
            download.setTaskStatus(STATUS.PAUSED);
            downloads.save(download);
        });
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
        try {
            int randomIndex = random.nextInt(ApplicationConstants.ports.length);
            int randomPort = ApplicationConstants.ports[randomIndex];
            options.setPort(randomPort);
        } catch (Exception e) {
            log.error("Error opening random free port falling back to 6891 {}", e.getMessage(), e);
            options.setPort(ApplicationConstants.ports[0]);
        }
        // options.setDhtPort(null);
        options.setDownloadAllFiles(true);
        log.info("{}", options);
        return options;
    }

    private static String createMagnetUri(String torrentHash) {
        return "magnet:?xt=urn:btih:" + torrentHash;
    }

}
