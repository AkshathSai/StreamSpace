package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.library.Movie;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="movies", collectionResourceRel="movies")
public interface MovieRepository extends ListCrudRepository<Movie, String> {

    Movie findByName(String name);

    boolean existsByContentId(String contentId);
}
