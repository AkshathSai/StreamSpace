package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.entity.Movie;
import org.springframework.content.commons.store.ContentStore;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.stereotype.Component;

@Component
@StoreRestResource
public interface MovieContentStore extends ContentStore<Movie, String> {

}
