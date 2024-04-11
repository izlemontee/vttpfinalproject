package izt.spotifyserver.services;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.config.WebSocketHandler;
import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.models.Chat;
import izt.spotifyserver.models.Message;
import izt.spotifyserver.repositories.MessagingNeo4JRepo;
import izt.spotifyserver.repositories.MessagingRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@Service
public class MessagingService {

    @Autowired
    private MessagingRepository messagingRepo;

    @Autowired
    private MessagingNeo4JRepo messagingNeo4jRepo;

    @Autowired
    private SpotifyApiService spotifyApiService;

    @Autowired
    private WebSocketHandler webSocketHandler;

    public String openChat(String user1, String user2){
        String id = "";
        if(!chatExists(user1, user2)){
            createNewChat(user1, user2);
        }
        id = getChatId(user1, user2);
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("id",id);
          
        
        return JOB.build().toString();
    }

    public void createNewChat(String user1, String user2){
        String id = UUID.randomUUID().toString().substring(0, 8);
        Chat chat = new Chat();
        chat.setId(id);
        chat.setUser1(user1);
        chat.setUser2(user2);
        try{
            createNewChatInSql(chat);
            createNewChatInNeo4j(chat);
        }catch(SQLFailedException ex){
            throw ex;
        }
    }

    @Transactional("transactionManager")
    public void createNewChatInSql(Chat chat){
        chat.setLast_updated(new Date().getTime());
        long count = messagingRepo.startNewChat(chat);
        if(count < 1){
            throw new SQLFailedException();
        }
    }

    public void createNewChatInNeo4j(Chat chat){
        messagingNeo4jRepo.save(chat);
    }

    public boolean chatExists(String user1, String user2){
        boolean exists = messagingRepo.chatExists(user1, user2);
        return exists;
    }

    public String getChatId(String user1, String user2){
        SqlRowSet rowset = messagingRepo.getChatId(user1, user2);
        rowset.next();
        String id = rowset.getString("id");
        return id;
    }

    public String getMessages(String id, int offset){
        SqlRowSet rowset = messagingRepo.getMessages(id, offset);
        
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        while(rowset.next()){
            String sender = rowset.getString("sender");
            String recipient = rowset.getString("recipient");
            String content = rowset.getString("content");
            long timestamp = rowset.getLong("timestamp");
            Message message = new Message(sender, recipient, content, timestamp);
            JsonObject messageJson = message.toJsonWithoutChatId();
            JAB.add(messageJson);
        }
        
        return JAB.build().toString();
    }

  
    public String sendMessage(String requestBody){
        JsonObject messageJson = Utils.stringToJson(requestBody);
        String sender = messageJson.getString("sender");
        String recipient = messageJson.getString("recipient");
        String content = messageJson.getString("content");
        String chat_id = messageJson.getString("chat_id");
        long timestamp = new Date().getTime();
        Message message = new Message(sender, recipient, content, timestamp);
        message.setChat_id(chat_id);
        try{
            sendMessageToSql(message);
            unreadChat(recipient, chat_id);
            updateChatListInRealTime(recipient, chat_id);
            String body = message.toJsonWithChatId().toString();
            updateLiveMessage(chat_id, body);
            return body;
        }catch(SQLFailedException ex){
            throw ex;
        }
    }

    public void updateLiveMessage(String id, String payload){
        try{
            webSocketHandler.pushToMessageChannels(id, payload);
        }catch(IOException ex){

        }
    }

    public void updateChatListInRealTime(String username, String id){
        String payload = getChatInfo(username, id);
        try{
            
            webSocketHandler.updateUnreadChats(username, payload);
            
        }catch(IOException ex){
           
        }
    }

    @Transactional("transactionManager")
    public void sendMessageToSql(Message message){
        long count = messagingRepo.sendMessage(message);
        long timestampcount = messagingRepo.updateChatTimestamp(message.getTimestamp(), message.getChat_id());
        if(count < 1 || timestampcount < 1){
            throw new SQLFailedException();
        }
    }

    public void readChat(String username, String id){
        messagingNeo4jRepo.deleteUnreadStatus(username, id);
        messagingNeo4jRepo.deleteReadStatus(username, id);
        messagingNeo4jRepo.readChat(username, id);
        updateNumberOfUnreadChats(username);
    }

    public void unreadChat(String username, String id){
        messagingNeo4jRepo.deleteReadStatus(username, id);
        messagingNeo4jRepo.deleteUnreadStatus(username, id);
        messagingNeo4jRepo.unreadChat(username, id);
        updateNumberOfUnreadChats(username);
    }

    public String getChatInfo(String username, String id){
        SqlRowSet rowset = messagingRepo.getChatInfo(id);
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JsonObject chatJson = JOB.build();
        while(rowset.next()){
            String user1 = rowset.getString("user1");
            String user2 = rowset.getString("user2");
            long last_updated = rowset.getLong("last_updated");
            Chat chat = new Chat();
            chat.setId(id);
            chat.setUser1(user1);
            chat.setUser2(user2);
            chat.setLast_updated(last_updated);
            chatJson = chatToJsonForUser(username, chat);
        }
        return chatJson.toString();

    }

    public String getChats(String username, int offset){
        SqlRowSet rowset = messagingRepo.getChats(username, offset);
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        while(rowset.next()){
            String id = rowset.getString("id");
            String user1 = rowset.getString("user1");
            String user2 = rowset.getString("user2");
            long last_updated = rowset.getLong("last_updated");
            Chat chat = new Chat();
            chat.setId(id);
            chat.setUser1(user1);
            chat.setUser2(user2);
            chat.setLast_updated(last_updated);
            JsonObject chatJson = chatToJsonForUser(username, chat);
            JAB.add(chatJson);
        }
        return JAB.build().toString();
    }

    public JsonObject chatToJsonForUser(String username, Chat chat){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        String usernameDisplay = "";
        if(!username.equals(chat.getUser1())){
            usernameDisplay = chat.getUser1();
        }else{
            usernameDisplay = chat.getUser2();
        }
        boolean read = messagingNeo4jRepo.checkReadStatus(username, chat.getId()).size()>0;
        JOB.add("id", chat.getId())
            .add("user1", chat.getUser1())
            .add("user2", chat.getUser2())
            .add("last_updated", chat.getLast_updated())
            .add("username_display", usernameDisplay)
            .add("image", spotifyApiService.getUserImageString(usernameDisplay))
            .add("read", read);

        return JOB.build();
        
    }

    public String getNumberOfUnreadChats(String username){
        Integer count = messagingNeo4jRepo.getNumberOfUnreadChats(username);
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("unread",count);
        return JOB.build().toString();
    }

    public void updateNumberOfUnreadChats(String username){
        try{
            int count = messagingNeo4jRepo.getNumberOfUnreadChats(username);
            webSocketHandler.updateUnreadChatsCount(username, count);
        }
        catch(IOException ex){

        }

    }

    public String getIdOfLatestChat(String username){
        SqlRowSet rowset = messagingRepo.getIdOfLatestChat(username);
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        while(rowset.next()){
            String id = rowset.getString("id");
            JOB.add("id",id);
        }
        return JOB.build().toString();
    }
    
}
