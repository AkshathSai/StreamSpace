package com.akshathsaipittala.streamspace.services.background;

import com.akshathsaipittala.streamspace.entity.DOWNLOADTYPE;
import com.akshathsaipittala.streamspace.entity.DownloadTask;
import com.akshathsaipittala.streamspace.entity.STATUS;
import com.akshathsaipittala.streamspace.repository.DownloadTaskRepository;
import com.akshathsaipittala.streamspace.repository.MovieRepository;
import com.akshathsaipittala.streamspace.repository.MusicRepository;
import com.akshathsaipittala.streamspace.services.torrentengine.Options;
import com.akshathsaipittala.streamspace.services.torrentengine.TorrentClient;
import com.akshathsaipittala.streamspace.utils.ApplicationConstants;
import com.akshathsaipittala.streamspace.utils.RuntimeHelper;
import com.akshathsaipittala.streamspace.utils.TorrentProgressHandler;
import com.akshathsaipittala.streamspace.utils.TorrentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class TorrentDownloadService {

    final RuntimeHelper runtimeHelper;
    final DownloadTaskRepository downloadTaskRepository;
    final TorrentProgressHandler torrentProgressHandler;
    final MovieRepository movieRepository;
    final MusicRepository musicRepository;

    @Async
    public void startDownload(DownloadTask downloadTask) {

        Options options = downloadTaskToOptions(downloadTask);

        try {
            TorrentClient.startEngine(options);
            downloadTask.setTaskStatus(STATUS.INPROGRESS);
            downloadTaskRepository.save(downloadTask);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public Options downloadTaskToOptions(DownloadTask downloadTask) {
        Options options = new Options();
        //options.setMetainfoFile(null);
        options.setMagnetUri(TorrentUtils.createMagnetUri(downloadTask.getTorrentHash()));
        options.setTargetDirectory(new File(runtimeHelper.getMediaFolders().get(downloadTask.getMediaType())));
        options.setSeedAfterDownloaded(false);
        options.setSequential(downloadTask.getDownloadType() == DOWNLOADTYPE.SEQUENTIAL);
        options.setEnforceEncryption(true);
        options.setDisableUi(true);
        options.setDisableTorrentStateLogs(false);
        options.setVerboseLogging(false);
        options.setTraceLogging(false);
        //options.setIface(null);
        try {
            options.setPort(TorrentUtils.getRandomFreePort());
        } catch (Exception e) {
            log.error("Error opening random free port falling back to 6891 {}", e.getMessage());
            options.setPort(ApplicationConstants.ports[0]);
        }
        //options.setDhtPort(null);
        options.setDownloadAllFiles(true);
        log.info("{}", options);
        return options;
    }

}
