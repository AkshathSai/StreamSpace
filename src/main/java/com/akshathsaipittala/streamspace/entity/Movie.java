package com.akshathsaipittala.streamspace.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Movie {

    @Id
    private String movieCode;
    private String name;
    @CreatedDate
    private LocalDateTime created;
    private String summary;
    @ContentId
    private String contentId;
    @ContentLength
    private long contentLength;
    @MimeType
    private String contentMimeType;
    private String mediaSource;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }

}
