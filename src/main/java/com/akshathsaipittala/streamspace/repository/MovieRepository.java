package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.entity.Movie;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path="movies", collectionResourceRel="movies")
public interface MovieRepository extends ListCrudRepository<Movie, String> {

    Movie findByName(String name);

    List<Movie> findByMediaSource(String source);

}
