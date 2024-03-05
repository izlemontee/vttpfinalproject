package izt.spotifyserver.controllers;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.services.SpotifyApiService;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin
public class ApiController {

    @Autowired
    private SpotifyApiService spotifyApiService;

    @GetMapping(path="/authenticate")
    public ResponseEntity<String> getLoginUri(){
        String clientId = spotifyApiService.getClientId();
        String uri = spotifyApiService.generateLoginUri();
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("clientid",clientId);
        JOB.add("uri",uri);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                        .body(JOB.build().toString());
        return response;

    }

    @GetMapping(path = "/callback")
    public String redirectAfterAuth(@RequestParam("code") String authKey, HttpServletResponse response)throws Exception{
        String tempId = spotifyApiService.redirectAfterAuth(authKey);
        
        response.sendRedirect("http://localhost:4200/redirect?id="+tempId);
        return tempId;
    }

    @PostMapping(path = "/addaccesskey")
    public ResponseEntity<String> addUserAccessKey(@RequestBody String requestBody){
        System.out.println(requestBody);
        int count = spotifyApiService.addUserAccessKey(requestBody);
        if(count>0){
            ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
            .body("{}");
            return response;
        }
        else{
            ResponseEntity<String> response = ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON)
            .body("{}");
            return response;
        }

    }

    @PostMapping(path = "/createuser")
    public ResponseEntity<String> createUser(@RequestBody String body){
        try{
            spotifyApiService.createUser(body);
            ResponseEntity<String> response = ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON)
                                                .body("{}");
            return response;

        }
        catch(SQLFailedException ex){
            String responseBody = Utils.getErrorJsonString(ex.getMessage());
            ResponseEntity<String> response = ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
                                                .body(responseBody);
            return response;
            

        }
    }

    @GetMapping(path = "/userexists")
    public ResponseEntity<String> userExists(@RequestParam String username){
        if(spotifyApiService.userExists(username)){
            ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
            .body("{}");
            return response;
        }
        else{
            ResponseEntity<String> response = ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON)
            .body("{}");
            return response;
        }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody String requestBody){
        boolean match = spotifyApiService.usernameAndPasswordMatch(requestBody);
        if(match){
            String responseBody = spotifyApiService.login(requestBody);
            ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
            .body(responseBody);
            return response;
        }
        else{
            ResponseEntity<String> response = ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
            .body("{}");
            return response;
        }
    }

    @DeleteMapping(path = "/logout")
    public ResponseEntity<String> logout(@RequestParam String id){
        if(spotifyApiService.logout(id)>0){
            ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
            .body("{}");
            return response;
        }else{
            ResponseEntity<String> response = ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
            .body("{}");
            return response;
        }

    }

    
}
