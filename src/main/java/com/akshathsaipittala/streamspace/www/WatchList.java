package com.akshathsaipittala.streamspace.www;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;

@Repository
public interface WatchList extends ListCrudRepository<Watch, Integer> {

    boolean existsByName(String name);

}

@Entity
@Getter
@Setter
@NoArgsConstructor
class Watch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String itemUrl;
    private String thumbnailUrl;
    @CreatedDate
    private Date addedDate;
}

@Controller
@RequestMapping("/watchlist")
@RequiredArgsConstructor
class WatchListController {

    final WatchList watchList;

    @HxRequest
    @GetMapping("")
    public HtmxResponse getWatchList(Model model) {
        model.addAttribute("watchlistitems", watchList.findAll());
        return HtmxResponse.builder()
                .view("watchlistitems")
                .build();
    }

    @HxRequest
    @PostMapping("")
    public ResponseEntity<String> addToWatchList(@RequestParam("movie-title") String movieTitle,
                                                 @RequestParam("item-url") String itemUrl,
                                                 @RequestParam("thumbnail-url") String thumbnailUrl) {

        if (!watchList.existsByName(movieTitle)) {
            Watch watch = new Watch();
            watch.setName(movieTitle);
            watch.setItemUrl(itemUrl);
            watch.setThumbnailUrl(thumbnailUrl);
            watch.setAddedDate(Date.valueOf(LocalDate.now()));
            watchList.save(watch);
        }

        return ResponseEntity.ok("<i class=\"bi bi-heart-fill\" style=\"color: #eb5282;\"></i> Added!");
    }

    @HxRequest
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFromWatchList(@PathVariable("id") int id) {
        watchList.deleteById(id);
        return ResponseEntity.ok("Deleted!");
    }
}