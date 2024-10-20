package com.akshathsaipittala.streamspace.www;

import com.akshathsaipittala.streamspace.resilience.RetryService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/yt")
@RequiredArgsConstructor
public class YouTubeController {

    final YoutubeCrawler youtubeCrawler;

    @GetMapping("/search")
    HtmxResponse search(@RequestParam String query, Model model) {
        // We pass the search query, scrape the search results and show it to the user
        // And we click on an interested item, It'll pass the videoId of that item to the search box
        // And the Search box will pull that particular video to play YT videos without any ads
        model.addAttribute("videos",youtubeCrawler.getVideos(query));
        return HtmxResponse.builder()
                .view("watchlistitems :: ytVideos")
                .build();
    }

    @GetMapping("/watch/{v}")
    HtmxResponse watch(@PathVariable("v") String v, Model model) {
        YouTubeResponseDTO dto = new YouTubeResponseDTO(null, v, null);
        model.addAttribute("youtubeTrailers", dto);
        return HtmxResponse.builder()
                .view("yt :: youtubeTrailer")
                .build();
    }


    @GetMapping("/crawl/trailer/{movie}")
    HtmxResponse getYoutubeTrailer(@PathVariable("movie") String movie, Model model) {
        YouTubeResponseDTO youTubeResponseDTO = youtubeCrawler.getYoutubeTrailersByTitle(movie);
        if (youTubeResponseDTO != null) {
            model.addAttribute("youtubeTrailers", youTubeResponseDTO);
            return HtmxResponse.builder()
                    .view("yt :: youtubeTrailer")
                    .build();
        } else {
            return HtmxResponse.builder()
                    .view("yt :: notFound")
                    .build();
        }
    }
}

record YouTubeResponseDTO (String title, String url, String thumbnailUrl) {}

@Slf4j
@Service
class YoutubeCrawler {

    private static final Pattern polymerInitialDataRegex = Pattern.compile("(window\\[\"ytInitialData\"]|var ytInitialData)\\s*=\\s*(.*);");

    YouTubeResponseDTO getYoutubeTrailersByTitle(String searchQuery) {

        Content content = crawlSearchResults(searchQuery)
                .twoColumnSearchResultsRenderer
                .primaryContents
                .sectionListRenderer.contents.getFirst().itemSectionRenderer.contents.getFirst();

        YouTubeResponseDTO youTubeResponseDTO = null;

        if (content.videoRenderer != null) {
            youTubeResponseDTO = new YouTubeResponseDTO(
                    content.videoRenderer.title.runs.getFirst().text,
                    content.videoRenderer.videoId,
                    null
            );
        }

        log.debug("{}", youTubeResponseDTO);
        return youTubeResponseDTO;
    }

    List<YouTubeResponseDTO> getVideos(String searchQuery) {
        Content content = crawlSearchResults(searchQuery);

        List<Content> contentList = new ArrayList<>(
                content
                        .twoColumnSearchResultsRenderer
                        .primaryContents
                        .sectionListRenderer
                        .contents.getFirst().itemSectionRenderer.contents
        );

        ArrayList<YouTubeResponseDTO> youTubeResponseDTOS = new ArrayList<>(contentList.size());

        contentList.stream()
                .filter(content1 -> content1.videoRenderer() != null)
                .forEach(
                        content1 -> youTubeResponseDTOS.add(
                                new YouTubeResponseDTO(
                                        content1.videoRenderer().title().runs().getFirst().text,
                                        content1.videoRenderer().videoId,
                                        content1.videoRenderer().thumbnail.thumbnails.getFirst().url)
                        )
                );
        return youTubeResponseDTOS;
    }

    private Content crawlSearchResults(String searchQuery) {
        RetryService<Content> retryService = new RetryService<>();

        return retryService.retry(() -> {
            Document document = Jsoup.connect("https://www.youtube.com/results?search_query=" + searchQuery + " trailer")
                    .get();

            // document.getElementsByTag("a").forEach(System.out::println); // This will get all links in the document
            // Match the JSON from the HTML. It should be within a script tag
            // String matcher0 = matcher.group(0);
            // String matcher1 = matcher.group(1);
            // String matcher2 = matcher.group(2);
            Matcher matcher = polymerInitialDataRegex.matcher(document.getElementsByTag("script").outerHtml());
            if (!matcher.find()) {
                log.warn("Failed to match ytInitialData JSON object");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(matcher.group(2));

            JsonNode contents = jsonNode.get("contents");
            return Objects.requireNonNull(objectMapper.treeToValue(contents, Content.class));
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Content(
            ItemSectionRenderer itemSectionRenderer,
            VideoRenderer videoRenderer,
            TwoColumnSearchResultsRenderer twoColumnSearchResultsRenderer
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ItemSectionRenderer(
            ArrayList<Content> contents
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record VideoRenderer(
            String videoId,
            Title title,
            Thumbnail thumbnail
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Thumbnail(
            ArrayList<Thumbnails> thumbnails
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Thumbnails(
            String url,
            String width,
            String height
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TwoColumnSearchResultsRenderer(
            PrimaryContents primaryContents
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record PrimaryContents(
            SectionListRenderer sectionListRenderer
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SectionListRenderer(
            ArrayList<Content> contents
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Title(
            ArrayList<Run> runs
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Run(
            String text
    ) {
    }
}
