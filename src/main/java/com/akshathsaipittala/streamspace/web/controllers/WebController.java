package com.akshathsaipittala.streamspace.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/progress")
    public String getDownloadProgress() {
        return "queue";
    }

}
