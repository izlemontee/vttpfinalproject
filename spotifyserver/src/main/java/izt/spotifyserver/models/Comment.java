package izt.spotifyserver.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Comment {

    // for comments on posts
    private int id;
    private String username;
    private String content;
    private long timestamp;
    private String post_id;
    private String profile_picture;
    public Comment(String username, String content, long timestamp, String post_id, String profile_picture) {
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.post_id = post_id;
        this.profile_picture = profile_picture;
    }
    public String getProfile_picture() {
        return profile_picture;
    }
    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
    public Comment(int id, String username, String content, long timestamp, String post_id) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.post_id = post_id;
    }
    public Comment(String username, String content, long timestamp, String post_id) {
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.post_id = post_id;
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
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getPost_id() {
        return post_id;
    }
    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public JsonObject toJsonWithId(){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("id",getId())
            .add("username",getUsername())
            .add("timestamp",getTimestamp())
            .add("content",getContent())
            .add("post_id",getPost_id())
            .add("profile_picture", getProfile_picture());
        return JOB.build();
    }

    public JsonObject toJson(){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("username",getUsername())
            .add("timestamp",getTimestamp())
            .add("content",getContent())
            .add("post_id",getPost_id())
            .add("profile_picture", getProfile_picture());
        return JOB.build();
    }
    
}
