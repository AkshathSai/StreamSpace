package com.akshathsaipittala.streamspace.web.api;

import com.akshathsaipittala.streamspace.entity.CONTENTTYPE;
import com.akshathsaipittala.streamspace.indexer.MediaIndexer;
import com.akshathsaipittala.streamspace.repository.MovieRepository;
import com.akshathsaipittala.streamspace.repository.MusicRepository;
import com.akshathsaipittala.streamspace.utils.RuntimeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/refresh")
@RequiredArgsConstructor
public class ContentRefreshAPI {

    final RuntimeHelper runtimeHelper;
    final MovieRepository movieRepository;
    final MusicRepository musicRepository;
    final MediaIndexer mediaIndexer;

    @Async
    @GetMapping("/personalmedia")
    public void refreshPersonalMedia() throws IOException {
        movieRepository.deleteAll();
        musicRepository.deleteAll();
        mediaIndexer.indexLocalMedia(
                runtimeHelper.getMediaFolders().get(CONTENTTYPE.VIDEO),
                runtimeHelper.getMediaFolders().get(CONTENTTYPE.AUDIO)
        );
    }

}
