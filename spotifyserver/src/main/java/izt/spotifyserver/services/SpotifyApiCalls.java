package izt.spotifyserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import izt.spotifyserver.Utils.Utils;
import izt.spotifyserver.models.User;
import izt.spotifyserver.repositories.UserSQLRepository;
import jakarta.json.JsonObject;
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
    
}
