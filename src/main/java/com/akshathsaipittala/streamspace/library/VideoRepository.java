package com.akshathsaipittala.streamspace.library;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(path="videos", collectionResourceRel="videos")
public interface VideoRepository extends ListCrudRepository<Video, String> {

    Video findByName(String name);

    boolean existsByContentId(String contentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Video")
    void bulkDeleteAll();
}
