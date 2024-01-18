package com.akshathsaipittala.streamspace.web.controllers;

import com.akshathsaipittala.streamspace.web.api.APIBayClient;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class MusicController {

    final APIBayClient apiBayClient;

    @HxRequest
    @GetMapping("/music")
    public HtmxResponse getFeaturedFLAC(Model model) {
        model.addAttribute("music", apiBayClient.getLosslessFLACAudio());
        return HtmxResponse.builder()
                .view("music :: featured-lossless-audio")
                .build();
    }
}
