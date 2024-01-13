package com.akshathsaipittala.streamspace.web.controllers;

import com.akshathsaipittala.streamspace.dto.yts.YTSMoviesRecord;
import com.akshathsaipittala.streamspace.web.api.YTSAPIClient;
import com.akshathsaipittala.streamspace.web.crawlers.YoutubeCrawlerService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.context.LazyContextVariable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class WebController {

    final YTSAPIClient ytsapiClient;
    final YoutubeCrawlerService youtubeCrawlerService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @HxRequest
    @GetMapping("/yts/movies/{id}")
    public HtmxResponse getNewOverview(@PathVariable("id") int id, Model model) {

        model.addAttribute("ytsMovieRecord", ytsapiClient.getMovieDetails(id));
        model.addAttribute("ytsSuggestedRecord", new LazyContextVariable<YTSMoviesRecord>() {
            @Override
            protected YTSMoviesRecord loadValue() {
                return ytsapiClient.getSuggestedMovies(id);
            }
        });

        return HtmxResponse.builder()
                .view("ytsMovie :: ytsMovieOverview")
                .build();
    }

    @HxRequest
    @GetMapping("/movie/{contentId}")
    public String getVideoPlayer(@PathVariable("contentId") String contentId, Model model) {
        model.addAttribute("contentId", URLEncoder.encode(contentId, StandardCharsets.UTF_8));
        return "player :: videoPlayer";
    }

    @HxRequest
    @GetMapping("/music/{contentId}")
    public String getMusicPlayer(@PathVariable("contentId") String contentId, Model model) {
        model.addAttribute("contentId", URLEncoder.encode(contentId, StandardCharsets.UTF_8));
        return "player :: musicPlayer";
    }

    @GetMapping("/progress")
    public String getDownloadProgress() {
        return "queue";
    }

}
