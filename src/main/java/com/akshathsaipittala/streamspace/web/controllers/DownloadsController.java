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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadsController {

    final DownloadTaskRepository downloadTaskRepository;

    @GetMapping("")
    public HtmxResponse getAllDownloads() {
        return HtmxResponse
                .builder()
                .view(new ModelAndView("downloads :: showAllDownloads", Map.of("tasks", downloadTaskRepository.findAll())))
                .build();
    }

    @ResponseBody
    @GetMapping(value = "/count", produces=MediaType.TEXT_HTML_VALUE)
    public String count() {
        return String.valueOf(downloadTaskRepository.count());
        //return "<span class=\"badge text-bg-secondary\">"+ downloadTaskRepository.count() +"</span>";
    }

    @GetMapping("/form")
    public HtmxResponse downloadForm() {

        return HtmxResponse.builder()
                .view("downloads :: downloadTorrent")
                .build();
    }

    /*@PostMapping("/togglePause")
    public ResponseEntity<String> togglePause() {
        TorrentClient.toggleStartStop();
        return ResponseEntity.ok("Toggled Pause! Click to Resume");
    }*/

    @HxRequest
    @PostMapping("/torrent")
    public HtmxResponse downloadTorrent(
            @RequestParam("selectedOption") String torrentHash,
            @RequestParam(value = "sequentialCheck", required = false) String sequentialCheck,
            @RequestParam(value = "torrentName", required = false) String torrentName,
            Model model) {

        log.info("Selected Option: {}", torrentHash);
        log.info("Strategy: {}", sequentialCheck);
        model.addAttribute("torrentHash", torrentHash);

        DownloadTask task;

        if (sequentialCheck != null && sequentialCheck.equals("on")) {
            task = new DownloadTask(torrentHash, torrentName !=null ? torrentName:torrentHash, torrentHash, STATUS.NEW, CONTENTTYPE.VIDEO, DOWNLOADTYPE.SEQUENTIAL);
        } else {
            task = new DownloadTask(torrentHash, torrentName !=null ? torrentName:torrentHash, torrentHash, STATUS.NEW, CONTENTTYPE.VIDEO, DOWNLOADTYPE.RANDOMIZED);
        }

        downloadTaskRepository.save(task);

        return HtmxResponse
                .builder()
                .view(new ModelAndView("downloads :: downloadProgress", Map.of("torrentHash", torrentHash)))
                .build();
    }

    @HxRequest
    @PostMapping("/torrent/{torrentHash}")
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
