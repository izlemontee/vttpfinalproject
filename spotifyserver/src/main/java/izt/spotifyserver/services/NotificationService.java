package izt.spotifyserver.services;



import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.config.WebSocketHandler;
import izt.spotifyserver.models.Notification;
import izt.spotifyserver.repositories.UserSQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@Service
public class NotificationService {

    @Autowired
    private UserSQLRepository userSQLRepository;

    @Autowired
    private WebSocketHandler webSocketHandler;

    public void addNewNotification(String requestBody)throws IOException{
        JsonObject jsonObject = Utils.stringToJson(requestBody);
        // recipient username
        String username = jsonObject.getString("username");
        String text = jsonObject.getString("text");
        boolean read = false;
        String url = jsonObject.getString("url");
        String type = jsonObject.getString("type");
        Date timestamp = new Date();
        Notification notification = new Notification(username, text, read, url, type, timestamp);
        userSQLRepository.addNewNotification(notification);
        Integer count = getNumberOfUnreadNotifications(username);
        try{
            webSocketHandler.updateUnreadNotifsCount(username, count);
        }catch(Exception ex){
            System.out.println("User not connected");
        }
     
    }

    public String processGetNotificationsRequest(SqlRowSet rowSet){
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        while(rowSet.next()){
            int id = rowSet.getInt("id");
            String notif_username = rowSet.getString("username");
            String text = rowSet.getString("text");
            String type = rowSet.getString("type");
            boolean notification_read = rowSet.getBoolean("notification_read");
            String url = rowSet.getString("url");
            Date timestamp = new Date(rowSet.getLong("timestamp"));

            Notification notification = new Notification(id, notif_username, text, notification_read, url, type, timestamp);
            JsonObject notificationJson = notification.toJsonObject();
            JAB.add(notificationJson);
        }

        return JAB.build().toString();
    }

    public String getNotificationsGlance(String username){
        SqlRowSet rowSet = userSQLRepository.getNotificationsGlance(username);
        String body = processGetNotificationsRequest(rowSet);
        return body;
    }
    
    public String getNotifications(String username, int offset){
        SqlRowSet rowset = userSQLRepository.getNotifications(username, offset);
        String body = processGetNotificationsRequest(rowset);
        return body;
    }

    public Optional<String> getSingleNotification(int id){
        SqlRowSet rowSet = userSQLRepository.getSingleNotification(id);

        if(rowSet.next()){
            Optional<String> optional = Optional.of(rowSet.getString("username"));
            return optional;
        }else{
            return Optional.empty();
        }
    }

    public void readNotification(int id)throws IOException{
        userSQLRepository.readNotification(id);
        Optional<String> optional = getSingleNotification(id);
        String username = optional.get();
        Integer count = getNumberOfUnreadNotifications(username);
        webSocketHandler.updateUnreadNotifsCount(username, count);
    }

    public String getNumberOfUnreadNotifsBody(String username){
        Integer count = getNumberOfUnreadNotifications(username);
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("count",count);
        return JOB.build().toString();
    }

    public Integer getNumberOfUnreadNotifications(String username){
        SqlRowSet rowSet = userSQLRepository.getNumberOfUnreadNotifications(username);
        int count = 0;
        if(rowSet.next()){
            count = rowSet.getInt("COUNT(notification_read)");
        }
        return count;
    }

    public void getNotificationType(){

    }
}
