package izt.spotifyserver.services;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.models.User;
import izt.spotifyserver.repositories.UserSQLRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import se.michaelthelin.spotify.SpotifyApi;

@Service
public class SpotifyApiCalls {

    @Value("${spotify.clientid}")
    private String clientId;

    @Value("${spotify.clientsecret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @Autowired
    private SpotifyApi spotifyApi;

    @Autowired
    private UserSQLRepository userSqlRepo;

    private String ACCESS_TOKEN="";

   
    private RestTemplate restTemplate = new RestTemplate();

    // does what it says, adds an access key and refresh token to new user
    public void addAccessKeyAndRefreshTokenToNewUser(User user){
        // also need to add spotify email
        // and to show that it has been linked to spotify
        String api_endpoint = "https://api.spotify.com/v1/me";
        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer "+ user.getAccessKey();
        headers.set("Authorization", bearer);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(api_endpoint,
                                            HttpMethod.GET
                                            ,entity,
                                            String.class
                                            );
        String responseBody = response.getBody();
        JsonObject responseJson = Utils.stringToJson(responseBody);
        String email = responseJson.getString("email");
        user.setEmail(email);
        userSqlRepo.addUserEmailAccessKeyRefreshToken(user);

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

    public String getUserTopGenres(String username, String duration){

        User user = getUserAccessKeyAndRefreshFromSql(username);
        String api_endpoint = "https://api.spotify.com/v1/me/top/artists";
        HttpHeaders headers = new HttpHeaders();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(api_endpoint)
                                                    .queryParam("limit", 50)
                                                    .queryParam("time_range", duration);
        String requestUri = uriComponentsBuilder.toUriString();
        Map<String,Object> params = new HashMap<>();
        params.put("limit",10);
        params.put("time_range",duration);
        String bearer = "Bearer "+ user.getAccessKey();
        headers.set("Authorization", bearer);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
       
        ResponseEntity<String> response = restTemplate.exchange(requestUri,
                                            HttpMethod.GET
                                            ,entity,
                                            String.class
                                            );
        // if the access key has expired
        if(response.getStatusCode().value() == 401){
            refreshUserAccessToken(user);
            String responseBody = getUserTopGenres(username, duration);
            return responseBody;
        }
        else if(response.getStatusCode().value() == 403){
            String body = response.getBody();
            return body;
        }
        String responseBody = response.getBody();
        JsonObject responseJson = Utils.stringToJson(responseBody);
        JsonArray items = responseJson.getJsonArray("items");
        List<String> genresAsString = new ArrayList<>();
        Map<String,Integer> genresAsMap = new HashMap<>();
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        if(items.size()>0){
            for(int i=0; i<items.size();i++){
                JsonObject item = items.getJsonObject(i);
                JsonArray genres = item.getJsonArray("genres");
                if(genres.size()>0){
                for(int j=0; j<genres.size(); j++){
                        String genre = genres.getJsonString(j).toString();
                        if(genresAsMap.containsKey(genre)){
                            int count = genresAsMap.get(genre);
                            genresAsMap.replace(genre, count+1);
                        }
                        else{
                            genresAsMap.put(genre,1);
                        }
                    }
                }
            }
        

            Map<String, Integer> sortedGenresMap = genresAsMap.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
            .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);
            
            Set<String> keys = sortedGenresMap.keySet();
            int length = 10;
            if(keys.size()<length){
                length = keys.size();
            }

            int counter = 0;

                for(String s:keys){
                    s = s.replace("\"","");
                    JAB.add(s);
                    counter++;
                    if(counter == length){
                        break;
                    }
                }
            
        }

        return JAB.build().toString();
        
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

    // use this for generic API calls
    public String getGenericAccessToken(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    
        // Set up the request body with client credentials and grant type
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(requestBody,headers);
    
        // call API and get response
        ResponseEntity<String> response = restTemplate.exchange("https://accounts.spotify.com/api/token",
                                                        HttpMethod.POST,
                                                        entity,
                                                        String.class);
    
        JsonObject jsonResponse = Utils.stringToJson(response.getBody());
        System.out.println("body:"+ response.getBody());
        String accessToken = jsonResponse.getString("access_token");
        ACCESS_TOKEN = accessToken;
        System.out.println(accessToken);
        return accessToken;
    }

    public String searchForArtistsUsingUrl(String requestBody){
        JsonObject bodyJson = Utils.stringToJson(requestBody);
        String url = bodyJson.getString("url");
        System.out.println("url: "+url);
        String body = searchForArtists(url);
        return body;
    }

    public String searchForArtistsByName(String name, int offset){
        String endpoint = "https://api.spotify.com/v1/search";
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(endpoint)
                                                    .queryParam("limit", 10)
                                                    .queryParam("q",name)
                                                    .queryParam("type", "artist")
                                                    .queryParam("market","SG")
                                                    .queryParam("offset", offset);
        String requestUri = uriComponentsBuilder.toUriString();
        String body = searchForArtists(requestUri);
        return body;
    }

    public String searchForArtists(String requestUri){
        HttpHeaders headers = new HttpHeaders();
        if(ACCESS_TOKEN==""){
            getGenericAccessToken();
        }
        String bearer = "Bearer "+ ACCESS_TOKEN;
        headers.set("Authorization", bearer);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(requestUri,
            HttpMethod.GET
            ,entity,
            String.class
            );
            JsonArrayBuilder JAB = Json.createArrayBuilder();
            JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
            JsonObject responseJson = Utils.stringToJson(response.getBody());
            // System.out.println(responseJson.get("artists").getClass());
            JsonObject parentJson = responseJson.getJsonObject("artists");
            String next = "";
            String prev = "";
            if(parentJson.get("next")!=null){
                next = parentJson.get("next").toString();
            }
            if(parentJson.get("previous")!=null){
 
                prev = parentJson.get("previous").toString();
            }
            JsonArray artistsJsonArray = parentJson.getJsonArray("items");
            if(artistsJsonArray.size()>0){
                for(JsonValue j:artistsJsonArray){
                    JsonObject artistJson = j.asJsonObject();
                    String url = artistJson.getJsonObject("external_urls").getString("spotify");
                    String artistName = artistJson.getString("name");
                    String image = "";
                    if(artistJson.get("images")!=null){
                        if(artistJson.getJsonArray("images").size()>0){
                            image = artistJson.getJsonArray("images").getJsonObject(0).getString("url");
                        }
                    }
                    JsonArray genres = artistJson.getJsonArray("genres");
                    String id = artistJson.getString("id");
                    JsonObjectBuilder JOB = Json.createObjectBuilder();
                    JOB.add("external_url", url)
                        .add("image",image)
                        .add("name",artistName)
                        .add("genres", genres)
                        .add("id", id);
                    JAB.add(JOB.build());
                }
            }
            responseBuilder.add("next",next)
                            .add("prev",prev)
                            .add("artists", JAB.build());
            return responseBuilder.build().toString();
            // return response.getBody();
        }catch(HttpClientErrorException ex){
            Integer errorCode = ex.getStatusCode().value();
            if(errorCode == 401){
                getGenericAccessToken();
                String body = searchForArtists(requestUri);
                return body;
            }else{
                String response = ex.getResponseBodyAsString();
                return response;
            }
        }
    }

}
