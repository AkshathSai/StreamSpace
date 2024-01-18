package com.akshathsaipittala.streamspace.web.controllers;

import com.akshathsaipittala.streamspace.entity.CONTENTTYPE;
import com.akshathsaipittala.streamspace.entity.DOWNLOADTYPE;
import com.akshathsaipittala.streamspace.entity.DownloadTask;
import com.akshathsaipittala.streamspace.entity.STATUS;
import com.akshathsaipittala.streamspace.repository.DownloadTaskRepository;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DownloadsController {

    final DownloadTaskRepository downloadTaskRepository;

    @GetMapping("/queue")
    public String getDownloadQueue(Model model) {
        model.addAttribute("tasks", downloadTaskRepository.findAll());
        return "queue";
    }

    @HxRequest
    @PostMapping("/download/torrent")
    public HtmxResponse downloadTorrent(
            @RequestParam("selectedOption") String torrentHash,
            @RequestParam(value = "sequentialCheck", required = false) String sequentialCheck,
            Model model
    ) {

        log.info("Selected Option: {}", torrentHash);
        log.info("Strategy: {}", sequentialCheck);
        model.addAttribute("torrentHash", torrentHash);

        DownloadTask task;

        if (sequentialCheck != null && sequentialCheck.equals("on")) {
            task = new DownloadTask(torrentHash, torrentHash, torrentHash, STATUS.NEW, CONTENTTYPE.VIDEO, DOWNLOADTYPE.SEQUENTIAL);
        } else {
            task = new DownloadTask(torrentHash, torrentHash, torrentHash, STATUS.NEW, CONTENTTYPE.VIDEO, DOWNLOADTYPE.RANDOMIZED);
        }

        downloadTaskRepository.save(task);

        return HtmxResponse
                .builder()
                .view(new ModelAndView("downloads :: downloadProgress", Map.of("torrentHash", torrentHash)))
                .build();
    }

    @HxRequest
    @PostMapping("/download/torrent/{torrentHash}")
    public HtmxResponse downloadTorrent(@PathVariable String torrentHash) {

        log.info("Selected Option: {}", torrentHash);

        DownloadTask task = new DownloadTask(torrentHash, torrentHash, torrentHash, STATUS.NEW, CONTENTTYPE.AUDIO, DOWNLOADTYPE.SEQUENTIAL);

        downloadTaskRepository.save(task);

        return HtmxResponse
                .builder()
                .view(new ModelAndView("downloads :: downloadProgress", Map.of("torrentHash", torrentHash)))
                .build();
    }

}
