package izt.spotifyserver.Utils;

import java.io.StringReader;
import java.util.UUID;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

public class Utils {

    public static JsonObject stringToJson(String text){
        JsonReader jr = Json.createReader(new StringReader(text));
        return jr.readObject();
    }

    public static String getUsernameFromClientRequest(String requestBody){
        JsonReader jr = Json.createReader(new StringReader(requestBody));
        JsonObject object = jr.readObject();
        return object.getString(("username"));
    }

    public static String getErrorJsonString(String message){
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("error", message);
        return JOB.build().toString();

    }

    public static String generateUUID(){
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "");
        return sessionId;
    }

    public static String generateShortUUID(){
        String id = UUID.randomUUID().toString().substring(0, 8);
        return id;
    }
    
}
