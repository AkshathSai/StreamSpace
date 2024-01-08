package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path="music", collectionResourceRel="music")
public interface MusicRepository extends JpaRepository<Music, String> {

    List<Music> findByMediaSource(String source);

}
