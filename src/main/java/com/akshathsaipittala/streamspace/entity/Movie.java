package com.akshathsaipittala.streamspace.entity;

import com.akshathsaipittala.streamspace.utils.HelperFunctions;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Movie {

    @Id
    private String movieCode;
    private String name;
    private Date created = new Date();
    private String summary;
    @ContentId
    private String contentId;
    @ContentLength
    private long contentLength;
    @MimeType
    private String contentMimeType;
    private String mediaSource;

    @PrePersist
    public void generateMovieCodeIfNotSet() {
        if (movieCode == null || movieCode.isEmpty()) {
            // Generate a unique movieCode here (e.g., using UUID)
            movieCode = HelperFunctions.generateUniqueCode();
        }
    }

}
