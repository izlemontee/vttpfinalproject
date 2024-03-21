package izt.spotifyserver.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Notification {
    private int id;
    // for the recipient
    private String username;
    private String text;
    private boolean notification_read;
    private String url;
    private String type;
    private long timestamp;

    public Notification(String username, String text, boolean notification_read, String url, String type,
            long timestamp) {
        this.username = username;
        this.text = text;
        this.notification_read = notification_read;
        this.url = url;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Notification(int id, String username, String text, boolean notification_read, String url, String type,
            long timestamp) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.notification_read = notification_read;
        this.url = url;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Notification(){
        
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public boolean isNotification_read() {
        return notification_read;
    }
    public void setNotification_read(boolean notification_read) {
        this.notification_read = notification_read;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public JsonObject toJsonObject(){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("id",getId())
            .add("username",getUsername())
            .add("text",getText())
            .add("url",getUrl())
            .add("type",getType())
            .add("timestamp",getTimestamp())
            .add("read",isNotification_read());
        return JOB.build();
    }
    
}
