package izt.spotifyserver.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import izt.spotifyserver.Utils.Utils;
import jakarta.json.JsonObject;

@Component
public class WebSocketHandler extends TextWebSocketHandler{

    private final String TYPE_SESSION_USERNAME = "session_username";

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    Map<String, List<WebSocketSession>> sessions = new HashMap<>();
    
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message){
        JsonObject payload = Utils.stringToJson(message.getPayload().toString());
        String type = payload.getString("type");
        switch(type){
            case TYPE_SESSION_USERNAME:{
                String username = payload.getString("username");
                addToSessions(username, session);
                break;
            }
        }
        
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        String origin = session.getHandshakeHeaders().getFirst("Origin");
        System.out.println("id: "+session.getId());
        logger.info("WebSocket connection established from origin: {}", origin);
    }

    public void addToSessions(String username, WebSocketSession session){
        if(sessions.containsKey(username)){
            sessions.get(username).add(session);
        }else{
            List<WebSocketSession> list = new ArrayList<>();
            list.add(session);
            sessions.put(username, list);
        }

    }
}
