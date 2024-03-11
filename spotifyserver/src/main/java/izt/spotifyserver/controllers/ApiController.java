package izt.spotifyserver.controllers;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.exceptions.UserNotFoundException;
import izt.spotifyserver.services.Neo4JUserService;
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

    @Autowired
    private Neo4JUserService neo4JService;

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

    @GetMapping(path = "/refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestParam String username){
        spotifyApiService.getUserAccessKeyAndRefreshFromSql(username);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body("{}");
        return response;
    }

    @GetMapping(path = "/topartists")
    public ResponseEntity<String> getTopArtists(@RequestParam String username, @RequestParam(required=false) String duration){
        if(duration == null){
            duration = "medium_term";
        }
        String body = spotifyApiService.getUserTopArtists(username, duration);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }

    @GetMapping(path = "/setupinit/{username}")
    public ResponseEntity<String> setupProfile(@PathVariable String username){
        String body = spotifyApiService.getUserNameAndBio(username);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body(body);
        return response;
    }

    @PostMapping(path = "/update/{username}")
    public ResponseEntity<String> updateProfile(@PathVariable String username, @RequestBody String body){
        spotifyApiService.updateProfileNameAndBio(body, username);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body("{}");
        return response;
    }


    @PostMapping(path="/update/{username}/artists")
    public ResponseEntity<String> updateArtists(@PathVariable String username, @RequestBody String body){
        try{
            spotifyApiService.addUserArtists(username, body);
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body("{}");

        return response;
        }
        catch(SQLFailedException ex){
        ResponseEntity<String> response = ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
            .body("{}");
    
        return response;
        }
    }

    @GetMapping(path = "/user/{username}")
    public ResponseEntity<String> getUserProfile(@PathVariable String username){
        try{
            String body = spotifyApiService.getUserProfile(username);
            ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
            .body(body);
            return response;
        }catch(UserNotFoundException ex){
            ResponseEntity<String> response = ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON)
            .body(ex.getMessage());
    
        return response;

        }
    }

    @GetMapping(path = "/userneo4j")
    public ResponseEntity<String> neo4jTest(){
        // spotifyApiService.addUserNeo4j();
        neo4JService.findUserInstruments();
        // System.out.println("user exists: "+neo4JService.userExists("izlemonteehjkhkhjk"));
        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
        .body("{}");
        return response;

    }
    
}
