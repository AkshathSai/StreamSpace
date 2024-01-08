package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path="movies", collectionResourceRel="movies")
public interface MovieRepository extends JpaRepository<Movie, String> {

    List<Movie> findByMediaSource(String source);

}
