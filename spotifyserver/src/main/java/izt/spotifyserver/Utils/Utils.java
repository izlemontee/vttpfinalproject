package izt.spotifyserver.Utils;

import java.io.StringReader;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import izt.spotifyserver.repositories.UserSQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

public class Utils {

    @Autowired
    private static UserSQLRepository userSQLRepository;

    public static JsonObject stringToJson(String text){
        JsonReader jr = Json.createReader(new StringReader(text));
        return jr.readObject();
    }

    public static JsonArray stringToJsonArray(String text){
        JsonReader jr = Json.createReader(new StringReader(text));
        return jr.readArray();
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

    public static boolean validSession(String sessionid, String username){
        return userSQLRepository.sessionValid(sessionid, username);
    }

    
}
