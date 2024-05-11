package com.akshathsaipittala.streamspace.web.controllers;

import com.akshathsaipittala.streamspace.dto.youtube.YouTubeResponseDTO;
import com.akshathsaipittala.streamspace.web.crawlers.YoutubeCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/yt")
@RequiredArgsConstructor
public class YouTubeController {

    final YoutubeCrawler youtubeCrawler;

    @GetMapping("/trailer/{movie}")
    public String getYoutubeTrailer(@PathVariable("movie") String movie, Model model) {
        YouTubeResponseDTO youTubeResponseDTO = youtubeCrawler.getYoutubeTrailersByTitle(movie);
        if (youTubeResponseDTO != null) {
            model.addAttribute("youtubeTrailers", youTubeResponseDTO);
            return "youtubetrailer";
        } else {
            return "";
        }
    }

}
