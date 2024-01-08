package com.akshathsaipittala.streamspace.entity;

import com.akshathsaipittala.streamspace.utils.RuntimeHelper;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Music {

    @Id
    private String musicId;
    private String name;
    @ContentId
    private String contentId;
    @ContentLength
    private long contentLength;
    private String summary;
    @MimeType
    private String contentMimeType = "audio/mpeg";
    private String mediaSource;

    @PrePersist
    public void randomGenerateMusicIdIfNotSet() {
        if (musicId == null || musicId.isEmpty()) {
            // Generate a unique movieCode here (e.g., using UUID)
            musicId = RuntimeHelper.generateUniqueCode();
        }
    }

}