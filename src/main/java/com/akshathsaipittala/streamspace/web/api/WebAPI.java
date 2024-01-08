package com.akshathsaipittala.streamspace.web.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class WebAPI {

    final YTSAPIClient ytsapiClient;

    @GetMapping("/search/yts")
    public String ytsSearch(@RequestParam("term") String term, Model model) {
        if(term == null) {
            return "";
        } else {
            model.addAttribute("results", ytsapiClient.ytsSearchV2(term).data());
            return "index :: search-results";
        }
    }

    @GetMapping("/search/v1/yts")
    public String ytsSearchV2(@RequestParam("term") String term, Model model) {
        if(term == null) {
            return "";
        } else {
            model.addAttribute("results", ytsapiClient.ytsSearchV2(term).data());
            return "browse :: showResults";
        }
    }

}
