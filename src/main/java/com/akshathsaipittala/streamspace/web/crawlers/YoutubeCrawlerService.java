package com.akshathsaipittala.streamspace.web.crawlers;

import com.akshathsaipittala.streamspace.dto.youtube.YouTubeResponseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Lazy
@Slf4j
@Service
public class YoutubeCrawlerService {

    public YouTubeResponseDTO getYoutubeTrailersByTitle(String searchQuery) {
        YouTubeResponseDTO youTubeResponseDTO = null;
        try {

            Document document = Jsoup.connect("https://www.youtube.com/results?search_query=" + searchQuery + " trailer")
                    .get();

            Elements scriptElements = document.getElementsByTag("script");
            String json = scriptElements.get(34).data();
            json = json.replace("var ytInitialData = ", "");

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            JsonNode contents = jsonNode.get("contents");

            Content content = objectMapper.treeToValue(contents, Content.class);

            youTubeResponseDTO = new YouTubeResponseDTO(
                    content
                            .twoColumnSearchResultsRenderer
                            .primaryContents
                            .sectionListRenderer
                            .contents.getFirst().itemSectionRenderer.contents.getFirst().videoRenderer.title.runs.getFirst().text,

                    content
                            .twoColumnSearchResultsRenderer
                            .primaryContents
                            .sectionListRenderer
                            .contents.getFirst().itemSectionRenderer.contents.getFirst().videoRenderer.videoId
            );

        } catch (IOException ex) {
            log.error("Error in fetching Youtube Trailer " + ex);
        }
        return youTubeResponseDTO;
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
            Title title
    ) {
    }

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
