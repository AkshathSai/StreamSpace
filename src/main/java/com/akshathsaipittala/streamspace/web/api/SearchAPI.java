package com.akshathsaipittala.streamspace.web.api;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchAPI {

    final YTSAPIClient ytsapiClient;
    final APIBayClient apiBayClient;

    @GetMapping("/search/yts")
    public String ytsSearch(@RequestParam("term") String term, Model model) {
        if(term == null) {
            return "";
        } else {
            model.addAttribute("results", ytsapiClient.ytsSearchV2(term).data());
            return "index";
        }
    }

    @HxRequest
    @GetMapping("/search/yts")
    public String ytsSearchAsync(@RequestParam("term") String term, Model model) {
        if(term == null) {
            return "";
        } else {
            model.addAttribute("results", ytsapiClient.ytsSearchV2(term).data());
            model.addAttribute("musicResults", apiBayClient.searchMusic(term));
            return "index :: search-results";
        }
    }

}
