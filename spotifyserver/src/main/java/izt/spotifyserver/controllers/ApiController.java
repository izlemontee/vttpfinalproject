package izt.spotifyserver.controllers;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(path="/login")
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
    // @GetMapping(path="/login")
    // public String getLoginUriString(){
    //     String uri = spotifyApiService.generateLoginUri();
    //     return uri;
    // }

    @GetMapping(path = "/callback")
    public String redirectAfterAuth(@RequestParam("code") String authKey, HttpServletResponse response)throws Exception{
        String accessToken = spotifyApiService.redirectAfterAuth(authKey);
        
        System.out.println("reached here");
        System.out.println(accessToken);
        response.sendRedirect("http://localhost:4200/redirect");
        return accessToken;
    }

    @PostMapping(path="/userAuthKey")
    public ResponseEntity<String> getUserAuthKey(@RequestBody String body){
        JsonReader jsonReader = Json.createReader(new StringReader(body));
        JsonObject jsonObject = jsonReader.readObject();

        ResponseEntity<String> response = ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                                            .body("{}");
        return response;
    }
    
}
