package com.akshathsaipittala.streamspace.services;

import com.akshathsaipittala.streamspace.services.torrentengine.NewTorrentClient;
import com.akshathsaipittala.streamspace.services.torrentengine.Options;
import com.akshathsaipittala.streamspace.services.torrentengine.TorrentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackgroundDownloadTask {

    //private final NewTorrentClient newTorrentClient;

    public void startDownload(Options options) {
        //TorrentClient.startEngine(options);
        NewTorrentClient newTorrentClient = new NewTorrentClient();
        newTorrentClient.startTorrent(options);
    }

}
