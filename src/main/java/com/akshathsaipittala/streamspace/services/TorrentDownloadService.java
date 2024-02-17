package com.akshathsaipittala.streamspace.services;

import com.akshathsaipittala.streamspace.entity.DOWNLOADTYPE;
import com.akshathsaipittala.streamspace.entity.DownloadTask;
import com.akshathsaipittala.streamspace.entity.STATUS;
import com.akshathsaipittala.streamspace.repository.DownloadTaskRepository;
import com.akshathsaipittala.streamspace.repository.MovieRepository;
import com.akshathsaipittala.streamspace.repository.MusicRepository;
import com.akshathsaipittala.streamspace.services.torrentengine.Options;
import com.akshathsaipittala.streamspace.services.torrentengine.TorrentClient;
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
    final RandomizedDownloader randomizedDownloader;
    final SequentialDownloader sequentialDownloader;
    final MovieRepository movieRepository;
    final MusicRepository musicRepository;

    @Async
    public void startDownload(DownloadTask downloadTask) {

        Options options = new Options(null,
                TorrentUtils.createMagnetUri(downloadTask.getTorrentHash()),
                //downloadTask.getDownloadLocation(),
                //new File(runtimeHelper.getMoviesFolderPath()),
                new File(runtimeHelper.getMediaFolders().get(downloadTask.getMediaType())),
                false,
                downloadTask.getDownloadType() == DOWNLOADTYPE.SEQUENTIAL,
                true,
                true,
                false,
                false,
                false,
                null,
                null,
                null,
                true);

        try {
            TorrentClient.startEngine(options);
            downloadTask.setTaskStatus(STATUS.INPROGRESS);
            downloadTaskRepository.save(downloadTask);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

}
