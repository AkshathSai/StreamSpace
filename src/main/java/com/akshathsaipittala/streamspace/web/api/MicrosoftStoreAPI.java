package com.akshathsaipittala.streamspace.web.api;

import com.akshathsaipittala.streamspace.dto.microsoft.MicrosoftStoreRecord;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("https://apps.microsoft.com/api/Reco/GetMovieProductsList?gl=US&hl=en-us&listName=")
public interface MicrosoftStoreAPI {

    @GetExchange("video.newreleases.movies&pgNo={pgNo}&noItems={noItems}&mediaType={mediaType}&filteredCategories={filteredCategories}&studioFilter=")
    MicrosoftStoreRecord newReleases(@PathVariable(value = "pgNo") int pgNo,
                                     @PathVariable(value = "noItems") int noItems,
                                     @PathVariable(value = "mediaType") String mediaType,
                                     @PathVariable(value = "filteredCategories") String filteredCategories);

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
    // @GetExchange("video.newreleases.movies&pgNo=1&noItems=12&mediaType=movies&filteredCategories=Comedy&studioFilter=")
    // MicrosoftStoreRecord newTopComedy();

    @GetExchange("video.toprated.movies&pgNo={pgNo}&noItems={noItems}&mediaType={mediaType}&filteredCategories={filteredCategories}&studioFilter=")
    MicrosoftStoreRecord topRated(@PathVariable(value = "pgNo") int pgNo,
                                  @PathVariable(value = "noItems") int noItems,
                                  @PathVariable(value = "mediaType") String mediaType,
                                  @PathVariable(value = "filteredCategories") String filteredCategories);

    @GetExchange("video.topselling.movies&pgNo={pgNo}&noItems={noItems}&mediaType={mediaType}&filteredCategories={filteredCategories}&studioFilter=")
    MicrosoftStoreRecord topSelling(@PathVariable(value = "pgNo") int pgNo,
                                    @PathVariable(value = "noItems") int noItems,
                                    @PathVariable(value = "mediaType") String mediaType,
                                    @PathVariable(value = "filteredCategories") String filteredCategories);

    @GetExchange("video.toprented.movies&pgNo={pgNo}&noItems={noItems}&mediaType={mediaType}&filteredCategories={filteredCategories}&studioFilter=")
    MicrosoftStoreRecord topRented(@PathVariable(value = "pgNo") int pgNo,
                                   @PathVariable(value = "noItems") int noItems,
                                   @PathVariable(value = "mediaType") String mediaType,
                                   @PathVariable(value = "filteredCategories") String filteredCategories);

    @GetExchange("video.collections.promo_marvelmovies&pgNo=1&noItems=48&mediaType=movies&filteredCategories=AllProducts")
    MicrosoftStoreRecord MCUCollection();

}
