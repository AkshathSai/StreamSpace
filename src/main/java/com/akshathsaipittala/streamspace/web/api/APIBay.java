package com.akshathsaipittala.streamspace.web.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/v2")
public class APIBay {

    RestTemplate restTemplate = new RestTemplate();

    //https://localhost:8080/v2/apiBay/FLAC%202023/101 - Music

    @GetMapping("/apiBay/{query}/{catId}")
    public APIBayTorrent[] searchPirateBay(@PathVariable("query") String query,
                                      @PathVariable("catId") String catId) {
        return restTemplate.getForObject("https://apibay.org/q.php?q=" + query + "&&cat=" + catId, APIBayTorrent[].class);
    }

    @GetMapping("/apiBay/{query}/")
    public APIBayTorrent[] getHDMovies(@PathVariable("query") String query) {
        return restTemplate.getForObject("https://apibay.org/q.php?q=" + query + "&&cat=207", APIBayTorrent[].class);
    }

    @GetMapping("/apiBay/tvshows/{query}/")
    public APIBayTorrent[] getHDTVShows(@PathVariable("query") String query) {
        return restTemplate.getForObject("https://apibay.org/q.php?q=" + query + "&&cat=208", APIBayTorrent[].class);
    }

    private record APIBayTorrent(String id,
                         String name,
                         String info_hash,
                         String leechers,
                         String seeders,
                         String num_files,
                         String size,
                         String username,
                         String added,
                         String status,
                         String category,
                         String imdb) {
    }
}
