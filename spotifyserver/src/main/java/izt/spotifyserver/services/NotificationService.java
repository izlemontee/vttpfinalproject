package izt.spotifyserver.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.models.Notification;
import izt.spotifyserver.repositories.UserSQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Service
public class NotificationService {

    @Autowired
    private UserSQLRepository userSQLRepository;

    public void addNewNotification(String requestBody){
        JsonObject jsonObject = Utils.stringToJson(requestBody);
        // recipient username
        String username = jsonObject.getString("username");
        String text = jsonObject.getString("text");
        boolean read = false;
        String url = jsonObject.getString("url");
        String type = jsonObject.getString("type");
        long timestamp = (new Date()).getTime();
        Notification notification = new Notification(username, text, read, url, type, timestamp);
        
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
            long timestamp = rowSet.getLong("timestamp");
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
    
    public String getNotifications(String username){
        SqlRowSet rowset = userSQLRepository.getNotifications(username);
        String body = processGetNotificationsRequest(rowset);
        return body;
    }


    public void getNotificationType(){

    }
}
