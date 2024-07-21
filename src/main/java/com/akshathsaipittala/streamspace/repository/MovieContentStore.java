package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.library.Movie;
import org.springframework.content.commons.store.ContentStore;
import org.springframework.content.rest.StoreRestResource;

@StoreRestResource
public interface MovieContentStore extends ContentStore<Movie, String> {

}
