package izt.spotifyserver.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Message {

    private String sender;
    private String recipient;
    private String content;
    public Message(String sender, String recipient, String content, long timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
    }

    private String chat_id;
    private long timestamp;
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getChat_id() {
        return chat_id;
    }
    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public JsonObject toJsonWithoutChatId(){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("sender",getSender())
            .add("recipient",getRecipient())
            .add("content", getContent())
            .add("timestamp",getTimestamp());
        return JOB.build();
    
    }
    
}
