package com.akshathsaipittala.streamspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WatchListItems {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
    @SequenceGenerator(name = "seqGen", sequenceName = "watchlist_id_seq", allocationSize = 1)
    private Integer id;
    private String name;
    private String itemUrl;
    private String thumbnailUrl;
    @CreatedDate
    private Date addedDate;
}
