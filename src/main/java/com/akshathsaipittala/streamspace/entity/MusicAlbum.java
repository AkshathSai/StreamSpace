package com.akshathsaipittala.streamspace.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class MusicAlbum {
    @Id
    private String albumName;
    private String artist;
    @OneToMany(mappedBy="album")
    private List<Song> songs;

}
