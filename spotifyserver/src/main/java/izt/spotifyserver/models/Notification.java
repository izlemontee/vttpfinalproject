package izt.spotifyserver.models;

public class Notification {
    private String id;
    private String username;
    private String text;
    private boolean notification_read;
    private String url;
    private String type;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
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
    
}
