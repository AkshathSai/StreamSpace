package com.akshathsaipittala.streamspace.services.background;

import com.akshathsaipittala.streamspace.services.torrentengine.Options;
import com.akshathsaipittala.streamspace.services.torrentengine.TorrentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackgroundDownloadTask {

    public void startDownload(Options options) {
        TorrentClient.startEngine(options);
    }

}
