package com.akshathsaipittala.streamspace.web.api;

import com.akshathsaipittala.streamspace.dto.youtube.YouTubeResponseDTO;
import com.akshathsaipittala.streamspace.web.crawlers.YoutubeCrawlerService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.LazyContextVariable;

import java.util.concurrent.CompletableFuture;

@Async
@Controller
@RequestMapping("yt")
@RequiredArgsConstructor
public class YouTubeAPI {

    final YoutubeCrawlerService youtubeCrawlerService;

    @HxRequest
    @GetMapping("/trailer/{movie}")
    public CompletableFuture<String> getYoutubeTrailer(@PathVariable("movie") String movie, Model model) {

        model.addAttribute("youtubeTrailers", new LazyContextVariable<YouTubeResponseDTO>() {
            @Override
            protected YouTubeResponseDTO loadValue() {
                return youtubeCrawlerService.getYoutubeTrailersByTitle(movie);
            }
        });

        return CompletableFuture.completedFuture("youtubeTrailer");
    }

}
