package com.akshathsaipittala.streamspace.web.controllers;

import com.akshathsaipittala.streamspace.entity.WatchListItems;
import com.akshathsaipittala.streamspace.repository.WatchListRepository;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;

@Controller
@RequestMapping("/watchlist")
@RequiredArgsConstructor
public class WatchListController {

    final WatchListRepository watchListRepository;

    @HxRequest
    @GetMapping("")
    public HtmxResponse getWatchList(Model model) {
        model.addAttribute("watchlistitems", watchListRepository.findAll());
        return HtmxResponse.builder()
                .view("watchlistitems")
                .build();
    }

    @HxRequest
    @PostMapping("")
    public ResponseEntity<String> addToWatchList(@RequestParam("movie-title") String movieTitle,
                                         @RequestParam("item-url") String itemUrl,
                                         @RequestParam("thumbnail-url") String thumbnailUrl) {

        if (!watchListRepository.existsByName(movieTitle)) {
            WatchListItems watchListItems = new WatchListItems();
            watchListItems.setName(movieTitle);
            watchListItems.setItemUrl(itemUrl);
            watchListItems.setThumbnailUrl(thumbnailUrl);
            watchListItems.setAddedDate(Date.valueOf(LocalDate.now()));
            watchListRepository.save(watchListItems);
        }

        return ResponseEntity.ok("<i class=\"bi bi-heart-fill\" style=\"color: #eb5282;\"></i> Added!");
    }

    @HxRequest
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFromWatchList(@PathVariable("id") int id) {
        watchListRepository.deleteById(id);
        return ResponseEntity.ok("Deleted!");
    }

}
