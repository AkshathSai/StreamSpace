package com.akshathsaipittala.streamspace.web.api;

import com.akshathsaipittala.streamspace.dto.microsoft.MicrosoftStoreRecord;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("https://apps.microsoft.com/api/Reco/GetMovieProductsList?gl=US&hl=en-us&listName=")
public interface MicrosoftStoreAPI {

    @GetExchange("video.newreleases.movies&pgNo=1&noItems=48&mediaType=movies&filteredCategories=AllProducts")
    MicrosoftStoreRecord newReleases();

    /** Available Filters
     *  Action/Adventure
     *  Animation
     *  Anime
     *  Comedy
     *  Documentary
     *  Drama
     *  Family
     *  Foreign/Independent
     *  Horror
     *  Other
     *  Romance
     *  Romantic Comedy
     *  Sci-Fi/Fantasy
     *  Sports
     *  Thriller/Mystery
     *  TV Movies
     */
    // Sample for applying Filters
    @GetExchange("video.newreleases.movies&pgNo=1&noItems=12&mediaType=movies&filteredCategories=Comedy&studioFilter=")
    MicrosoftStoreRecord newTopComedy();

    @GetExchange("video.toprated.movies&pgNo=1&noItems=12&mediaType=movies&filteredCategories=AllProducts&studioFilter=")
    MicrosoftStoreRecord topRated();

    @GetExchange("video.topselling.movies&pgNo=1&noItems=12&mediaType=movies&filteredCategories=AllProducts&studioFilter=")
    MicrosoftStoreRecord topSelling();

    @GetExchange("video.toprented.movies&pgNo=1&noItems=12&mediaType=movies&filteredCategories=AllProducts&studioFilter=")
    MicrosoftStoreRecord topRented();

    @GetExchange("video.collections.promo_marvelmovies&pgNo=1&noItems=48&mediaType=movies&filteredCategories=AllProducts")
    MicrosoftStoreRecord MCUCollection();

}
