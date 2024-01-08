package com.akshathsaipittala.streamspace.entity;

import com.akshathsaipittala.streamspace.listener.DownloadTaskListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.AbstractAggregateRoot;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@EntityListeners(DownloadTaskListener.class)
public class DownloadTask extends AbstractAggregateRoot<DownloadTask> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    public DownloadTask(String torrentHash, String torrentName, String movieCode, STATUS taskStatus, CONTENTTYPE mediaType, DOWNLOADTYPE downloadType) {
        this.torrentHash = torrentHash;
        this.torrentName = torrentName;
        this.movieCode = movieCode;
        this.taskStatus = taskStatus;
        this.mediaType = mediaType;
        this.downloadType = downloadType;
    }
}