package com.akshathsaipittala.streamspace.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDetails {

    private LocalDateTime timeStamp;
    private String message;
    private String details;

}
