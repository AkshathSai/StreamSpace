package com.akshathsaipittala.streamspace.services;

import com.akshathsaipittala.streamspace.entity.CONTENTTYPE;
import com.akshathsaipittala.streamspace.indexer.MediaIndexer;
import com.akshathsaipittala.streamspace.utils.RuntimeHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

        // 3C88F31D82729DBF83A702BD536A376B23DB5EC6
    }

}
