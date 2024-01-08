package com.akshathsaipittala.streamspace.dto.piratebay;

import com.akshathsaipittala.streamspace.dto.piratebay.Torrent;
import java.util.List;

public record TorrentResponse(List<Torrent> torrents) {
}
