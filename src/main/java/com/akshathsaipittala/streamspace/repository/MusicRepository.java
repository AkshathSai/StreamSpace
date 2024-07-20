package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.library.Song;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="music", collectionResourceRel="music")
public interface MusicRepository extends ListCrudRepository<Song, String> {

    boolean existsByContentId(String contentId);
}
