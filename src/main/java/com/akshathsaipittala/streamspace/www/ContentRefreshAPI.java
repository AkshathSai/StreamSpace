package com.akshathsaipittala.streamspace.www;

import com.akshathsaipittala.streamspace.library.Indexer;
import com.akshathsaipittala.streamspace.repository.MovieRepository;
import com.akshathsaipittala.streamspace.repository.MusicRepository;
import com.akshathsaipittala.streamspace.services.ContentDirectoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashSet;

@RestController
@RequestMapping("/refresh")
@RequiredArgsConstructor
public class ContentRefreshAPI {

    final MovieRepository movieRepository;
    final MusicRepository musicRepository;
    final Indexer indexer;

    @Async
    @GetMapping("/personalmedia")
    public void refreshPersonalMedia() throws IOException {
        movieRepository.deleteAll();
        musicRepository.deleteAll();
        indexer.indexLocalMedia(new HashSet<>(ContentDirectoryServices.mediaFolders.values()));
    }

}
