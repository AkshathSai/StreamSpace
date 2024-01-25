package com.akshathsaipittala.streamspace.web.controllers;

import com.akshathsaipittala.streamspace.dto.yts.YTSMoviesRecord;
import com.akshathsaipittala.streamspace.web.api.YTSAPIClient;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.LazyContextVariable;

@Slf4j
@Controller
@RequestMapping("/yts")
@RequiredArgsConstructor
public class YTSMoviesController {

    final YTSAPIClient ytsapiClient;

    @HxRequest
    @GetMapping("/movies/{id}")
    public HtmxResponse getNewOverview(@PathVariable("id") int id, Model model) {

        model.addAttribute("ytsMovieRecord", ytsapiClient.getMovieDetails(id));
        model.addAttribute("ytsSuggestedRecord", new LazyContextVariable<YTSMoviesRecord>() {
            @Override
            protected YTSMoviesRecord loadValue() {
                return ytsapiClient.getSuggestedMovies(id);
            }
        });

        return HtmxResponse.builder()
                .view("ytsMovie :: ytsMovieOverview")
                .build();
    }

    @GetMapping("/movies/{id}")
    public String getYTSMovieOverview(@PathVariable("id") int id, Model model) {

        model.addAttribute("ytsMovieRecord", ytsapiClient.getMovieDetails(id));
        model.addAttribute("ytsSuggestedRecord", new LazyContextVariable<YTSMoviesRecord>() {
            @Override
            protected YTSMoviesRecord loadValue() {
                return ytsapiClient.getSuggestedMovies(id);
            }
        });

        return "ytsMovie";
    }

    @GetMapping("/movies/cat/{category}")
    public HtmxResponse viewAllPage(Model model,
                              @RequestParam(defaultValue = "1") int page,
                              @PathVariable("category") String category) {

        model.addAttribute("category", category);

        if (category.equals("latest")) {
            model.addAttribute("ytsMoviesRecord", new LazyContextVariable<YTSMoviesRecord>() {
                @Override
                protected YTSMoviesRecord loadValue() {
                    return ytsapiClient.getLatestMovies(page);
                }
            });
        } else if (category.equals("mostliked")) {
            model.addAttribute("ytsMoviesRecord", new LazyContextVariable<YTSMoviesRecord>() {
                @Override
                protected YTSMoviesRecord loadValue() {
                    return ytsapiClient.getMostLiked(page);
                }
            });
        } else if (category.equals("imdbrating")) {
            model.addAttribute("ytsMoviesRecord", new LazyContextVariable<YTSMoviesRecord>() {
                @Override
                protected YTSMoviesRecord loadValue() {
                    return ytsapiClient.getIMDBHighestRated(page);
                }
            });
        } else if (category.equals("mostwatched")) {
            model.addAttribute("ytsMoviesRecord", new LazyContextVariable<YTSMoviesRecord>() {
                @Override
                protected YTSMoviesRecord loadValue() {
                    return ytsapiClient.getMostWatchedMovies(page);
                }
            });
        } else if (category.equals("latestcomedies")) {
            model.addAttribute("ytsMoviesRecord", new LazyContextVariable<YTSMoviesRecord>() {
                @Override
                protected YTSMoviesRecord loadValue() {
                    return ytsapiClient.getLatestComedyMovies(page);
                }
            });
        } else if (category.equals("mustwatch")) {
            model.addAttribute("ytsMoviesRecord", new LazyContextVariable<YTSMoviesRecord>() {
                @Override
                protected YTSMoviesRecord loadValue() {
                    return ytsapiClient.getMustWatch(page);
                }
            });
        }

        model.addAttribute("currentPage", page);

        return HtmxResponse.builder()
                .view("viewAll :: gallery")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsMostWatched")
    public HtmxResponse ytsMostWatched(Model model) {
        model.addAttribute("ytsMostWatchedRecord", new LazyContextVariable<YTSMoviesRecord>() {
            @Override
            protected YTSMoviesRecord loadValue() {
                return ytsapiClient.getMostWatchedMovies();
            }
        });

        return HtmxResponse.builder()
                .view("movies :: ytsMostWatched")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsLatest")
    public HtmxResponse ytsLatest(Model model) {
        model.addAttribute("ytsLatestRecord", new LazyContextVariable<YTSMoviesRecord>() {
            @Override
            protected YTSMoviesRecord loadValue() {
                return ytsapiClient.getLatestMovies();
            }
        });

        return HtmxResponse.builder()
                .view("movies :: ytsLatest")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsMostLiked")
    public HtmxResponse ytsMostLiked(Model model) {
        model.addAttribute("ytsMostLikedRecord", new LazyContextVariable<YTSMoviesRecord>() {
            @Override
            protected YTSMoviesRecord loadValue() {
                return ytsapiClient.getMostLiked();
            }
        });

        return HtmxResponse.builder()
                .view("movies :: ytsMostLiked")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsImdbRating")
    public HtmxResponse ytsImdbRating(Model model) {
        model.addAttribute("ytsIMDBHighestRatedRecord", new LazyContextVariable<YTSMoviesRecord>() {
            @Override
            protected YTSMoviesRecord loadValue() {
                return ytsapiClient.getIMDBHighestRated();
            }
        });

        return HtmxResponse.builder()
                .view("movies :: ytsImdbRating")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsLatestComedies")
    public HtmxResponse ytsLatestComedies(Model model) {
        model.addAttribute("ytsLatestComedyRecord", new LazyContextVariable<YTSMoviesRecord>() {
            @Override
            protected YTSMoviesRecord loadValue() {
                return ytsapiClient.getLatestComedyMovies();
            }
        });

        return HtmxResponse.builder()
                .view("movies :: ytsLatestComedies")
                .build();
    }

    @HxRequest
    @GetMapping("/ytsMustWatch")
    public HtmxResponse ytsMustWatch(Model model) {

        model.addAttribute("ytsMustWatchRecord", new LazyContextVariable<YTSMoviesRecord>() {
            @Override
            protected YTSMoviesRecord loadValue() {
                return ytsapiClient.getMustWatch();
            }
        });

        return HtmxResponse.builder()
                .view("movies :: ytsMustWatch")
                .build();
    }

}
