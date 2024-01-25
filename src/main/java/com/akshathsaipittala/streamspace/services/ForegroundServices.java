package com.akshathsaipittala.streamspace.services;

import com.akshathsaipittala.streamspace.indexer.LocalMediaIndexer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Async
@Slf4j
@Component
public class ForegroundServices {

    @Lazy
    @Autowired
    private LocalMediaIndexer localMediaIndexer;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() throws IOException {
        log.info("Indexing Local Media");
        localMediaIndexer.indexMedia();
    }

     /*@Async
    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.MINUTES)
    public void printHeapUsage() {
        log.info(Stats.printHeapMemoryUsage());
    }*/

    /*@Async
    @CacheEvict(allEntries = true, value = {"getMostWatchedMovies"})
    @Scheduled(fixedDelay = 45, timeUnit = TimeUnit.MINUTES)
    public void clearCache() {
        log.info("Cache cleared");
    }*/

}
