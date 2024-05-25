package com.akshathsaipittala.streamspace.web.controllers;

import org.progressify.spring.annotations.StaleWhileRevalidate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    @StaleWhileRevalidate
    public String index() {
        return "index";
    }

}
