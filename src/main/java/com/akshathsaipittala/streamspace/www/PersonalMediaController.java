package com.akshathsaipittala.streamspace.www;

import com.akshathsaipittala.streamspace.library.VideoRepository;
import com.akshathsaipittala.streamspace.library.MusicRepository;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/personal")
@RequiredArgsConstructor
public class PersonalMediaController {

    final VideoRepository videoRepository;
    final MusicRepository musicRepository;

    @HxRequest
    @GetMapping("/media")
    HtmxResponse getTitles(Model model) {

        model.addAttribute("videos", videoRepository.findAll());
        model.addAttribute("music", musicRepository.findAll());

        return HtmxResponse.builder()
                .view("personalmedia :: personalMediaPlayer")
                .build();
    }

}
