package com.akshathsaipittala.streamspace.library;

import org.springframework.content.commons.store.ContentStore;
import org.springframework.content.rest.StoreRestResource;

@StoreRestResource
public interface VideoContentStore extends ContentStore<Video, String> {

}
