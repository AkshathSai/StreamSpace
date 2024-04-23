package com.akshathsaipittala.streamspace.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
public class MusicAlbum {
    @Id
    private String albumName;
    private String artist;
    @OneToMany(mappedBy="album")
    private List<Song> songs;

}
