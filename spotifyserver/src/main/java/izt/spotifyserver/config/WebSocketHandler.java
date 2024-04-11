package izt.spotifyserver.config;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import izt.spotifyserver.Utils.Utils;
import jakarta.json.JsonObject;

@Component
public class WebSocketHandler extends TextWebSocketHandler{

    private final String TYPE_SESSION_USERNAME = "session_username";
    private final String TYPE_SESSION_MESSAGE = "session_message";
    private final String TYPE_SESSION_CHATS = "session_chats";

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    Map<String, List<WebSocketSession>> sessions = new HashMap<>();
    Map<String, List<WebSocketSession>> messageSessions = new HashMap<>();
    Map<String, List<WebSocketSession>> chatsSessions = new HashMap<>();
    
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

            case TYPE_SESSION_MESSAGE:{
                String username = payload.getString("username");
                addToMessageSessions(username, session);
                break;
            }

            case TYPE_SESSION_CHATS:{
                String username = payload.getString("username");
                addToChatsSessions(username, session);
            }
            
        }
        
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        String origin = session.getHandshakeHeaders().getFirst("Origin");
        System.out.println("New connection");
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

    public void addToMessageSessions(String username, WebSocketSession session){
        if(messageSessions.containsKey(username)){
            messageSessions.get(username).add(session);
        }else{
            List<WebSocketSession> list = new ArrayList<>();
            list.add(session);
            messageSessions.put(username, list);
        }
    }

    public void addToChatsSessions(String username, WebSocketSession session){
        if(chatsSessions.containsKey(username)){
            chatsSessions.get(username).add(session);
        }else{
            List<WebSocketSession> list = new ArrayList<>();
            list.add(session);
            chatsSessions.put(username, list);
        }
        logger.info(chatsSessions.toString());
    }

    public void updateUnreadNotifsCount(String username, Integer count)throws IOException{
        List<WebSocketSession> sessionsList = sessions.get(username);
        List<WebSocketSession> newList = new ArrayList<>();
        for(WebSocketSession ws:sessionsList){
            TextMessage textMessage = new TextMessage(count.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(textMessage);
            try{
                ws.sendMessage(new TextMessage(jsonString));
                newList.add(ws);
            }catch(IllegalStateException ex){
                ws.close();
            }
        }
        sessions.replace(username, newList);
    }

    public void updateUnreadChatsCount(String username, Integer count)throws IOException{
        List<WebSocketSession> sessionsList = messageSessions.get(username);
        List<WebSocketSession> newList = new ArrayList<>();
        try{
            for(WebSocketSession ws:sessionsList){
                TextMessage textMessage = new TextMessage(count.toString());
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(textMessage);
                try{
                    ws.sendMessage(new TextMessage(jsonString));
                    newList.add(ws);
                }catch(IllegalStateException ex){
                    ws.close();
                }
            }
            sessions.replace(username, newList);
        }catch(NullPointerException ex){
            
        }
    }

    public void updateUnreadChats(String username, String id)throws IOException{
        
        List<WebSocketSession> chatsList = chatsSessions.get(username);
        logger.info("username, list: "+username+chatsList);
        List<WebSocketSession> newList = new ArrayList<>();
        try{
            for(WebSocketSession ws:chatsList){
                System.out.println("sending...");
                TextMessage textMessage = new TextMessage(id.toString());
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(textMessage);
                try{
                    ws.sendMessage(new TextMessage(jsonString));
                    newList.add(ws);
                }catch(IllegalStateException ex){
                    ws.close();
                }
            }
            sessions.replace(username, newList);
        }catch(NullPointerException ex){
            logger.info("nullpointerexception: " + ex.getMessage());
        }
    }
}
