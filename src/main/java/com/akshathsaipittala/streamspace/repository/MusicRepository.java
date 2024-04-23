package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.entity.Song;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path="music", collectionResourceRel="music")
public interface MusicRepository extends ListCrudRepository<Song, String> {

    List<Song> findByMediaSource(String source);

    boolean existsByContentId(String contentId);
}
