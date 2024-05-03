package com.akshathsaipittala.streamspace.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Lazy
@Slf4j
@Getter
@Setter
@Component
public class TorrentProgressHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Get the torrentHash from the session attributes
        String torrentHash = (String) session.getAttributes().get("torrentHash");
        // Store the session using torrentHash as the key
        sessions.put(torrentHash, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String torrentId = (String) session.getAttributes().get("torrentHash");
        sessions.remove(torrentId);
        super.afterConnectionClosed(session, CloseStatus.NORMAL);
    }

    public void sendProgressUpdate(String torrentId, String message, String downloaded, String uploaded, int peerCount, String remainingTime, boolean complete) throws IOException {
        WebSocketSession session = sessions.get(torrentId);
        if (session != null && session.isOpen()) {
            String webResponse = "<div id=\"progress-bar\" class=\"progress progress-bar\" role=\"progressbar\" hx-swap-oob=\"true\" style=\"width: " + message + ";height:5px;\" aria-valuenow=\""+ message +"\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div><div id=\"torrent-stats\" class=\"container\"> <div class=\"row\"> <div class=\"col\"> <p class=\"text-body-secondary\">" + message + "</p></div> <div class=\"col\"> <p class=\"text-body-secondary\"><i class=\"bi bi-arrow-down\"></i> "+ downloaded +"</p> </div> <div class=\"col\"> <p class=\"text-body-secondary\"><i class=\"bi bi-arrow-up\"></i> "+ uploaded +"</p> </div> <div class=\"col\"> <p class=\"text-body-secondary\">"+peerCount+"P</p> </div> <div class=\"col\"> <p class=\"text-body-secondary\">ETA "+remainingTime+"</p> </div> </div> </div>";
            try {
                session.sendMessage(new TextMessage(webResponse));
                if (complete) {
                    session.close(CloseStatus.NORMAL);
                    log.info("Closed WebSocket");
                }
            } catch (IOException e) {
                log.error("Error sending progress update or closing session for torrentId: {}", torrentId, e);
                // Optionally, remove the session from the map if it's no longer valid
                sessions.remove(torrentId);
            }
        }
    }
}
