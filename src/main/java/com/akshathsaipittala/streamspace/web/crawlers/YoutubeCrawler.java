package com.akshathsaipittala.streamspace.web.crawlers;

import com.akshathsaipittala.streamspace.dto.youtube.YouTubeResponseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class YoutubeCrawler {

    private static final Pattern polymerInitialDataRegex = Pattern.compile("(window\\[\"ytInitialData\"]|var ytInitialData)\\s*=\\s*(.*);");

    public YouTubeResponseDTO getYoutubeTrailersByTitle(String searchQuery) throws IOException {

        Document document = Jsoup.connect("https://www.youtube.com/results?search_query=" + searchQuery + " trailer")
                .get();

        // Match the JSON from the HTML. It should be within a script tag
        Matcher matcher = polymerInitialDataRegex.matcher(document.getElementsByTag("script").outerHtml());
        if (!matcher.find()) {
            log.warn("Failed to match ytInitialData JSON object");
        }

//        String matcher0 = matcher.group(0);
//        String matcher1 = matcher.group(1);
//        String matcher2 = matcher.group(2);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(matcher.group(2));

        JsonNode contents = jsonNode.get("contents");

        Content content = objectMapper.treeToValue(contents, Content.class);

        YouTubeResponseDTO youTubeResponseDTO = new YouTubeResponseDTO(
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
        log.debug("{}", youTubeResponseDTO);
        return Objects.requireNonNull(youTubeResponseDTO);
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
