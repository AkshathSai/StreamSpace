package com.akshathsaipittala.streamspace.entity;

import com.akshathsaipittala.streamspace.listener.DownloadTaskListener;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@EntityListeners(DownloadTaskListener.class)
public class DownloadTask extends AbstractAggregateRoot<DownloadTask> {

    @Id
    private String torrentHash;
    private String torrentName;
    private String movieCode;
    private double progress = 0;
    @Enumerated(EnumType.STRING)
    private STATUS taskStatus;
    @Enumerated(EnumType.STRING)
    private CONTENTTYPE mediaType;
    @Enumerated(EnumType.STRING)
    private DOWNLOADTYPE downloadType;
    @CreatedDate
    private Date createdDate;

    public DownloadTask(String torrentHash, String torrentName, String movieCode, STATUS taskStatus, CONTENTTYPE mediaType, DOWNLOADTYPE downloadType) {
        this.torrentHash = torrentHash;
        this.torrentName = torrentName;
        this.movieCode = movieCode;
        this.taskStatus = taskStatus;
        this.mediaType = mediaType;
        this.downloadType = downloadType;
        this.createdDate = Date.valueOf(LocalDate.now());
    }
}