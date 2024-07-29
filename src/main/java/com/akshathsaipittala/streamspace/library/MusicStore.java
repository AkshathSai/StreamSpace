package com.akshathsaipittala.streamspace.library;

import org.springframework.content.commons.store.ContentStore;
import org.springframework.content.rest.StoreRestResource;

@StoreRestResource(path="music")
public interface MusicStore extends ContentStore<Song, String> {}