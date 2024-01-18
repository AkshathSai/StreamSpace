package com.akshathsaipittala.streamspace.web.api;

import com.akshathsaipittala.streamspace.dto.apibay.APIBayTorrent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "https://apibay.org/", accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface APIBayClient {

    //@GetExchange("https://apibay.org/q.php?q=FLAC 2023&&cat=101")
    @GetExchange("q.php?q=FLAC&&cat=101")
    APIBayTorrent[] getLosslessFLACAudio();

    @GetExchange("q.php?q=ALAC&&cat=101")
    APIBayTorrent[] getLosslessALACAudio();

    @GetExchange("q.php?q=Hi-Res&&cat=101")
    APIBayTorrent[] getHiResAudio();

    @GetExchange("q.php?q={term}&&cat=101")
    APIBayTorrent[] searchMusic(@PathVariable String term);

}
