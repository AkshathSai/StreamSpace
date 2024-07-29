package com.akshathsaipittala.streamspace.www;

import com.akshathsaipittala.streamspace.library.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequestMapping("/stream")
@RequiredArgsConstructor
public class StreamController {

    final VideoRepository videoRepository;

    @GetMapping("/video/{movieCode}")
    public String getVideoPlayer(@PathVariable("movieCode") String movieCode, Model model) {
        model.addAttribute("movieCode", movieCode);
        var video = videoRepository.findById(URLDecoder.decode(movieCode, StandardCharsets.UTF_8));
        var contentMimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        // Workaround to get MKV Videos playing on Chromium browsers
        if (video.get().getContentMimeType().equals("video/x-matroska")) {
            contentMimeType = "video/webm";
        } else if (video.get().getContentMimeType().equals("video/mp4")) {
            contentMimeType = "video/mp4";
        }

        model.addAttribute("contentMimeType", contentMimeType);
        return "player :: videoPlayer";
    }

    @GetMapping("/music/{contentId}")
    public String getMusicPlayer(@PathVariable("contentId") String contentId, Model model) {
        model.addAttribute("contentId", URLDecoder.decode(contentId, StandardCharsets.UTF_8));
        return "player :: musicPlayer";
    }

}
