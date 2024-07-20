package com.akshathsaipittala.streamspace.repository;

import com.akshathsaipittala.streamspace.library.Song;
import org.springframework.content.commons.store.ContentStore;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.stereotype.Component;

@Component
@StoreRestResource(path="music")
public interface MusicStore extends ContentStore<Song, String> {}