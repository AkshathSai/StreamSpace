package com.akshathsaipittala.streamspace.library;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(path="music", collectionResourceRel="music")
public interface MusicRepository extends ListCrudRepository<Song, String> {

    boolean existsByContentId(String contentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Song")
    void bulkDeleteAll();
}
