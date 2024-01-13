package com.akshathsaipittala.streamspace.web.api;

import com.akshathsaipittala.streamspace.dto.apibay.APIBayTorrent;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface APIBayClient {

    //@GetExchange("https://apibay.org/q.php?q=FLAC 2023&&cat=101")
    @GetExchange("https://apibay.org/q.php?q=FLAC&&cat=101")
    APIBayTorrent[] getLosslessAudio();


}
