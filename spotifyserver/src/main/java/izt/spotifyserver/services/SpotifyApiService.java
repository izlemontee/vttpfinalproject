package izt.spotifyserver.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.exceptions.SQLFailedException;
import izt.spotifyserver.exceptions.UserNotFoundException;
import izt.spotifyserver.models.Artist;
import izt.spotifyserver.models.Neo4jUser;
import izt.spotifyserver.models.User;
import izt.spotifyserver.repositories.UserNeo4jRepository;
import izt.spotifyserver.repositories.UserSQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
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
    private UserNeo4jRepository userNeo4j;

    @Autowired
    private SpotifyApi spotifyApi;

    @Autowired
    private SpotifyApiCalls apiCalls;

   
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
        String refreshToken = authorizationCodeCredentials.getRefreshToken();
        User user = new User();
        user.setAccessKey(token);
        user.setRefreshToken(refreshToken);
        
        String tempId = Utils.generateShortUUID();
        temporaryUserMap.put(tempId, user);
        return tempId;

    }

    public User getUserAccessKeyAndRefreshFromSql(String username){
        SqlRowSet rowSet = userSqlRepo.getUserAccessKeyAndRefresh(username);
        rowSet.next();
        String accessKey = rowSet.getString("access_key");
        String refreshToken = rowSet.getString("refresh_token");
        User user = new User();
        user.setAccessKey(accessKey);
        user.setRefreshToken(refreshToken);
        user.setUsername(username);
        return user;

    }

    public void refreshUserAccessToken(User user){
        String url = "https://accounts.spotify.com/api/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String credentials = clientId + ":" + clientSecret;
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        headers.setBasicAuth(base64Credentials);

        // request body
        MultiValueMap<String,String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type","refresh_token");
        requestBody.add("refresh_token",user.getRefreshToken());
        requestBody.add("cilent_id",clientId);
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(requestBody,headers);

        

        // call API to get new key
        ResponseEntity<String> response = restTemplate.exchange(url,
                                                        HttpMethod.POST,
                                                        entity,
                                                        String.class);
        String responseBody = response.getBody();
        JsonObject responseBodyJson = Utils.stringToJson(responseBody);
        String accessKey = responseBodyJson.getString("access_token");
        user.setAccessKey(accessKey);
        userSqlRepo.updateUserAccessKey(user);
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
            addUserSession(user);
        }
        return user.getSessionId();
    }

    public int addUserAccessKey(String requestBody){
        JsonObject jsonObject = Utils.stringToJson(requestBody);
        User user = temporaryUserMap.get(jsonObject.getString("tempId"));
        user.setUsername(jsonObject.getString("username"));
        int count = userSqlRepo.addAccessKey(user); 
        apiCalls.addAccessKeyAndRefreshTokenToNewUser(user);
        return count;
    }
    
    public String getUserTopArtists(String username, String duration){
        User user = getUserAccessKeyAndRefreshFromSql(username);
        String api_endpoint = "https://api.spotify.com/v1/me/top/artists";
        HttpHeaders headers = new HttpHeaders();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(api_endpoint)
                                                    .queryParam("limit", 10)
                                                    .queryParam("time_range", duration);
        String requestUri = uriComponentsBuilder.toUriString();
        Map<String,Object> params = new HashMap<>();
        params.put("limit",10);
        params.put("time_range",duration);
        String bearer = "Bearer "+ user.getAccessKey();
        headers.set("Authorization", bearer);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
       
        try{
        ResponseEntity<String> response = restTemplate.exchange(requestUri,
                                            HttpMethod.GET
                                            ,entity,
                                            String.class
                                            );
        String responseBody = response.getBody();
        return responseBody;
        }catch(HttpClientErrorException ex){
            Integer errorCode = ex.getStatusCode().value();

            // 401 is when the status code expires
            if(errorCode == 401){
                System.out.println("401 status code expired");
                refreshUserAccessToken(user);
                String responseBody = getUserTopArtists(username, duration);
                return responseBody;
            }else{
                String body = ex.getResponseBodyAsString();
                return body;
            }
        }
        
        // if the access key has expired
        // catch(Exception ex){
        //     refreshUserAccessToken(user);
        //     String responseBody = getUserTopArtists(username, duration);
        //     return responseBody;
        // }
    }

    public String getUserNameAndBio(String username){
        SqlRowSet rowset = userSqlRepo.getUserNameAndBio(username);
        rowset.next();
        String firstName = rowset.getString("firstname");
        String lastName = rowset.getString("lastname");
        String bio = "";
        boolean linked = rowset.getBoolean("spotify_linked");
        if(rowset.getString("bio") !=null){
            bio = rowset.getString("bio");
        }
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("firstName",firstName)
            .add("lastName",lastName)
            .add("bio",bio)
            .add("spotify_linked",linked);
        return JOB.build().toString();
    }

    public void updateProfileNameAndBio(String body, String username){
        JsonObject object = Utils.stringToJson(body);
        String bio = object.getString("bio");
        String firstName = object.getString("firstName");
        String lastName = object.getString("lastName");
        User user = new User();
        user.setUsername(username);
        user.setBio(bio);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        userSqlRepo.updateProfileNameAndBio(user);
    }

    // @Transactional
    public void addUserArtists(String username, String requestBody){
        // delete the artists first to avoid conflict
        userSqlRepo.deleteArtists(username);
        JsonArray jsonArray = Utils.stringToJsonArray(requestBody);
        
        int count = 0;
        for(int i = 0; i<jsonArray.size(); i++){
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            String name = jsonObject.getString("name");
            String external_url = jsonObject.getString("external_url");
            String image = jsonObject.getString("image");
            Artist artist = new Artist();
            artist.setImage(image);
            artist.setName(name);
            artist.setUrl(external_url);
            artist.setUsername(username);
            count += userSqlRepo.addArtist(artist);
        }
        if(count != jsonArray.size()){
            throw new SQLFailedException();
        }
    }

    public String getUserProfile(String username){
        if(userExists(username)){
            SqlRowSet rowSet = userSqlRepo.getUserProfile(username);
            rowSet.next();
            User user = new User();
            user.setUsername(username);
            user.setFirstName(rowSet.getString("firstname"));
            user.setLastName(rowSet.getString("lastname"));
            String bio = "";
            if(rowSet.getString("bio") != null){
                bio = rowSet.getString("bio");
            }
            String image = "https://t3.ftcdn.net/jpg/05/16/27/58/360_F_516275801_f3Fsp17x6HQK0xQgDQEELoTuERO4SsWV.jpg";
            if(rowSet.getString("image") !=null){
                image = rowSet.getString("image");
            }
            user.setBio(bio);
            user.setImageUrl(image);
            SqlRowSet artistRowSet = userSqlRepo.getArtists(username);
            List<Artist> artists = new LinkedList<>();
            while(artistRowSet.next()){
                Artist artist = new Artist();
                artist.setImage(artistRowSet.getString("image"));
                artist.setName(artistRowSet.getString("name"));
                artist.setUrl(artistRowSet.getString("url"));
                artists.add(artist);
            }

           
            JsonArrayBuilder JAB = Json.createArrayBuilder();
            for(Artist a: artists){
                JsonObjectBuilder JOB = Json.createObjectBuilder();
                JOB.add("image", a.getImage())
                    .add("name",a.getName())
                    .add("external_url",a.getUrl());
                JAB.add(JOB.build());
            }
            JsonObjectBuilder JOB = Json.createObjectBuilder();
            JOB.add("username", user.getUsername())
                .add("firstName",user.getFirstName())
                .add("lastName",user.getLastName())
                .add("bio",user.getBio())
                .add("image", user.getImageUrl())
                .add("artists",JAB.build());
            return JOB.build().toString();
        }
        else{
            throw new UserNotFoundException();
        }
    }

    public void neo4jTest(){
        // List<Neo4jUser> users = userNeo4j.findAll();
        // for(Neo4jUser u:users){
        //     System.out.println(u.toString());
        //     System.out.println(u.getUsername());
        // }
        // System.out.println("users:"+users.toString());
        Iterable<Neo4jUser> userIterable = userNeo4j.findUserByUsername("izlemontee");
        for(Neo4jUser u:userIterable){
            System.out.println(u.getUsername());
        }
    }

    public void addUserNeo4j(){
        // Neo4jUser neo4jUser = new Neo4jUser();
        // neo4jUser.setUsername("neo4juser");
        // neo4jUser.setFirstName("neo4j");
        // neo4jUser.setLastName("user");
        // userNeo4j.save(neo4jUser);
        neo4jTest();
    }
    
    public String getGenres(String username){
        SqlRowSet rowset = userSqlRepo.getGenres(username);
        JsonArrayBuilder genres = Json.createArrayBuilder();
        while(rowset.next()){
            String genre = rowset.getString("name");
            genres.add(genre);
        }
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JOB.add("genres", genres.build());
        return JOB.build().toString();
    }

    public void addGenres(String requestBody, String username){
        JsonArray genresArray = Utils.stringToJsonArray(requestBody);
        long count = 0;
        for(JsonValue v:genresArray){
            String genre = v.toString().replace("\"", "");
            count += userSqlRepo.addGenres(genre, username);
        }
    }

    public String getUserSearchResults(String searchTerm){
        SqlRowSet rowset = userSqlRepo.searchForUsers(searchTerm);
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        while(rowset.next()){
            String username = rowset.getString("username");
            String firstName = rowset.getString("firstname");
            String lastName = rowset.getString("lastname");
            String image = Utils.PLACEHOLDER_IMAGE;
            if(rowset.getString("image") != null){
                image = rowset.getString("image");
            }
            JsonObjectBuilder JOB = Json.createObjectBuilder();
            JOB.add("username",username)
                .add("firstName", firstName)
                .add("lastName",lastName)
                .add("image",image);
            JAB.add(JOB.build());
        }
        return JAB.build().toString();
    }

}
