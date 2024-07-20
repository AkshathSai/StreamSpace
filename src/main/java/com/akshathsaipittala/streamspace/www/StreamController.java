package com.akshathsaipittala.streamspace.www;

import lombok.extern.slf4j.Slf4j;
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
public class StreamController {

    @GetMapping("/movie/{movieCode}")
    public String getVideoPlayer(@PathVariable("movieCode") String movieCode, Model model) {
        model.addAttribute("movieCode", movieCode);
        return "player :: videoPlayer";
    }

    @GetMapping("/music/{contentId}")
    public String getMusicPlayer(@PathVariable("contentId") String contentId, Model model) {
        model.addAttribute("contentId", URLDecoder.decode(contentId, StandardCharsets.UTF_8));
        return "player :: musicPlayer";
    }

}
