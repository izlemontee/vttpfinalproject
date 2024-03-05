package izt.spotifyserver.services;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.models.User;
import izt.spotifyserver.repositories.UserSQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

@Service
public class SpotifyApiService {

    @Value("${spotify.clientid}")
    private String clientId;

    @Value("${spotify.clientsecret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @Autowired
    private SpotifyApi spotifyApi;

   
    private RestTemplate restTemplate = new RestTemplate();

    private Map<String,User> temporaryUserMap = new HashMap<>();


    @Autowired
    private UserSQLRepository userSqlRepo;

    public String getClientId(){
        return clientId;
    }

    // to generate the uri for login
    public String generateLoginUri(){
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                                                            .scope("user-read-private, user-read-email, user-top-read")
                                                            .show_dialog(true)
                                                            .build();
        URI uri = authorizationCodeUriRequest.execute();
        // String html = goToSpotifyLoginPage(uri);
        return uri.toString();
    }

    public String goToSpotifyLoginPage(URI uri){
        ResponseEntity<String> response =  restTemplate.getForEntity(uri, String.class);
        //System.out.println(response.getBody().toString());
        return response.getBody();
    }

    // use this for user-related stuff
    public String getUserAccessToken(String authKey)throws Exception{
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authKey).build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
        String userAccessToken = authorizationCodeCredentials.getAccessToken();

        return userAccessToken;

    }

    public String redirectAfterAuth(String authKey)throws Exception{
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authKey).build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
        String token = authorizationCodeCredentials.getAccessToken();
        User user = new User();
        user.setAccessKey(token);
        String tempId = Utils.generateShortUUID();
        temporaryUserMap.put(tempId, user);
        return tempId;

    }


    public void getUserAccessTokenFromClient(String authCode, String verifier){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type","authorization_code");
        requestBody.add("code",authCode);
        requestBody.add("redirect_uri","http://localhost:4200/redirect");
        requestBody.add("client_id",clientId);
        requestBody.add("verifier", verifier);
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(requestBody,headers);

        ResponseEntity<String> response = restTemplate.exchange("https://accounts.spotify.com/api/token",
                                                        HttpMethod.POST,
                                                        entity,
                                                        String.class);
        System.out.println(response.getBody());
    }

    @Transactional
    public void createUser(String body){
        JsonObject bodyJson = Utils.stringToJson(body);
        User user = new User();
        user.setUsername(bodyJson.getString("username"));
        user.setEmail(bodyJson.getString("email"));
        user.setFirstName(bodyJson.getString("firstName"));
        user.setLastName(bodyJson.getString("lastName"));
        user.setPassword(bodyJson.getString("password"));
        int insertCount = userSqlRepo.createNewUser(user);
        if(insertCount>0){

        }
        else{
            throw new SQLFailedException();
            
        }

    }

    public boolean userExists(String username){
        return userSqlRepo.userExists(username);
    }

    public boolean usernameAndPasswordMatch(String requestBody){
        JsonObject jsonObject = Utils.stringToJson(requestBody);
        User user = new User();
        user.setUsername(jsonObject.getString("username"));
        user.setPassword(jsonObject.getString("password"));
        boolean match = userSqlRepo.usernamePasswordMatch(user);
        return match;
    }

    public String login(String requestBody){
        JsonObject jsonObject = Utils.stringToJson(requestBody);
        User user = new User();
        user.setUsername(jsonObject.getString("username"));
        user.setPassword(jsonObject.getString("password"));
        String sessionid = addUserSession(user);
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("id",sessionid);
        return JOB.build().toString();
    }

    public int logout(String id){
        int count = userSqlRepo.deleterUserSession(id);
        return count;
    }

    public String addUserSession(User user){
        user.setSessionId(Utils.generateUUID());
        try{
            userSqlRepo.addUserSession(user);
        }catch(Exception ex){
            System.out.println("exception");
            addUserSession(user);
        }
        return user.getSessionId();
    }

    public int addUserAccessKey(String requestBody){
        JsonObject jsonObject = Utils.stringToJson(requestBody);
        User user = temporaryUserMap.get(jsonObject.getString("tempId"));
        user.setUsername(jsonObject.getString("username"));
        int count = userSqlRepo.addAccessKey(user); 
        return count;
    }
    
}
