package izt.spotifyserver.models;

import java.util.UUID;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;


@Node("Post")
public class Post {

    @Id
    private String id;
    private String username;
    private String content;
    private long timestamp;
    private boolean has_picture;
    private String profile_picture;

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public void generateId(){
        String id = UUID.randomUUID().toString().substring(0, 8);
        setId(id);
    }

    // to add to sql if no image
    public Post(String username, String content, long timestamp) {
        generateId();
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

     // to add to sql if have image
    public Post(String username, String content, long timestamp, boolean has_picture, String image_url) {
        generateId();
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.has_picture = has_picture;
        this.image_url = image_url;
    }

     // to take from sql if no image
    public Post(String id, String username, String content, long timestamp) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }
    private String image_url;
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
    public boolean isHas_picture() {
        return has_picture;
    }
    public void setHas_picture(boolean has_picture) {
        this.has_picture = has_picture;
    }
    public String getImage_url() {
        return image_url;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }



     // to take from sql if have image
    public Post(String id, String username, String content, long timestamp, boolean has_picture, String image_url) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.has_picture = has_picture;
        this.image_url = image_url;
    }

    public JsonObject toJson(){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("id",getId())
            .add("username",getUsername())
            .add("content",getContent())
            .add("timestamp",getTimestamp())
            .add("has_picture",isHas_picture());
        if(isHas_picture()){
            JOB.add("image_url",getImage_url());
        }
        else{
            JOB.add("image_url","");
        }

        return JOB.build();
    }

    public JsonObject toJsonNoImage(){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("id",getId())
            .add("username",getUsername())
            .add("content",getContent())
            .add("timestamp",getTimestamp())
            .add("profile_picture",getProfile_picture());

        return JOB.build();
    }

    public JsonObjectBuilder toJsonObjectBuilder(){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("id",getId())
            .add("username",getUsername())
            .add("content",getContent())
            .add("timestamp",getTimestamp())
            .add("has_picture",isHas_picture())
            .add("profile_picture",getProfile_picture());
        if(isHas_picture()){
            JOB.add("image_url",getImage_url());
        }
        else{
            JOB.add("image_url","");
        }
        

        return JOB;
    }

    

}
