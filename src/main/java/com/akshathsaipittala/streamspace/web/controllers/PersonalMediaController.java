package com.akshathsaipittala.streamspace.web.controllers;

import com.akshathsaipittala.streamspace.repository.MovieRepository;
import com.akshathsaipittala.streamspace.repository.MusicRepository;
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

    final MovieRepository movieRepository;
    final MusicRepository musicRepository;

    @HxRequest
    @GetMapping("/media")
    HtmxResponse getTitles(Model model) {

        model.addAttribute("videos", movieRepository.findAll());
        model.addAttribute("music", musicRepository.findAll());

        return HtmxResponse.builder()
                .view("localmedia :: local-media-player")
                .build();
    }

}
