package com.akshathsaipittala.streamspace.www;

import com.akshathsaipittala.streamspace.library.Indexer;
import com.akshathsaipittala.streamspace.library.VideoRepository;
import com.akshathsaipittala.streamspace.library.MusicRepository;
import com.akshathsaipittala.streamspace.services.ContentDirectoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@RequestMapping("/refresh")
@RequiredArgsConstructor
public class ContentRefreshAPI {

    final VideoRepository videoRepository;
    final MusicRepository musicRepository;
    final Indexer indexer;

    @GetMapping("/personalmedia")
    public void refreshPersonalMedia() {
        videoRepository.bulkDeleteAll();
        musicRepository.bulkDeleteAll();
        indexer.indexLocalMedia(new HashSet<>(ContentDirectoryServices.mediaFolders.values()));
    }

}
