package com.akshathsaipittala.streamspace.entity;

import com.akshathsaipittala.streamspace.utils.HelperFunctions;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Song {

    @Id
    private String songId;
    private String album;
    private String name;
    @ContentId
    private String contentId;
    @ContentLength
    private long contentLength;
    private String summary;
    @MimeType
    private String contentMimeType;
    private String mediaSource;

    @PrePersist
    public void randomGenerateSongIdIfNotSet() {
        if (songId == null || songId.isEmpty()) {
            // Generate a unique movieCode here (e.g., using UUID)
            songId = HelperFunctions.generateUniqueCode();
        }
    }

}